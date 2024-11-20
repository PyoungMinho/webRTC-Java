// WebSocket 연결 설정
const socket = new WebSocket("ws://localhost:8081/ws");

const myFace = document.getElementById('myFace');
const peerFace = document.getElementById('peerFace');
const muteBtn = document.getElementById("mute");
const cameraBtn = document.getElementById("camera");
const cameraSelect = document.getElementById("cameras");
const call = document.getElementById("call");

call.hidden = true;

let myStream;
let muted = false;
let cameraOff = false;
let roomName;
let myPeerConnection;

async function getCameras() {
    try {
        const devices = await navigator.mediaDevices.enumerateDevices(); // 사용(또는 접근)이 가능한 미디어 입력 장치나 출력장치들의 리스트
        const cameras = devices.filter(device => device.kind === "videoinput");
        const currentCamera = myStream.getVideoTracks()[0];
        cameras.forEach(camera => {
            const option = document.createElement("option");
            option.value = camera.deviceId;
            option.innerText = camera.label;
            if (currentCamera.label === camera.label) {
                option.selected = true;
            }
            cameraSelect.appendChild(option);
        });
    } catch (e) {
        console.log(e);
    }
}

async function getMedia(deviceId) {
    const initialConstrains = {
        audio: true,
        video: { facingMode: "user" }, // 셀프 카메라 설정
    };
    const cameraConstraints = {
        audio: true,
        video: { deviceId: { exact: deviceId } },
    };

    try {
        myStream = await navigator.mediaDevices.getUserMedia(
            deviceId ? cameraConstraints : initialConstrains
        );
        myFace.srcObject = myStream;
        if (!deviceId) {
            await getCameras();
        }
    } catch (e) {
        console.log(e);
    }
}

getMedia();

function handleMuteClick() {
    myStream.getAudioTracks().forEach(track => (track.enabled = !track.enabled));
    muteBtn.innerText = muted ? "Mute" : "Unmute";
    muted = !muted;
}

function handleCameraClick() {
    myStream.getVideoTracks().forEach(track => (track.enabled = !track.enabled));
    cameraBtn.innerText = cameraOff ? "Turn Camera Off" : "Turn Camera On";
    cameraOff = !cameraOff;
}

async function handleCameraChange() {
    await getMedia(cameraSelect.value);
    if (myPeerConnection) {
        const videoTrack = myStream.getVideoTracks()[0];
        const videoSender = myPeerConnection.getSenders().find(sender => sender.track.kind === "video");
        videoSender.replaceTrack(videoTrack);
    }
}

muteBtn.addEventListener("click", handleMuteClick);
cameraBtn.addEventListener("click", handleCameraClick);
cameraSelect.addEventListener("input", handleCameraChange);

const welcomeForm = document.querySelector("form");
const welcome = document.getElementById("welcome");

async function initCall() {
    welcome.hidden = true;
    call.hidden = false;
    await getMedia();
    makeConnection();
}

async function handleWelcomeSubmit(event) {
    event.preventDefault();
    const input = document.querySelector("input");
    roomName = input.value;
    await initCall();
    socket.send(JSON.stringify({ type: "join_room", roomName }));
    input.value = "";
}

welcomeForm.addEventListener("submit", handleWelcomeSubmit);

socket.onopen = () => {
    console.log("WebSocket is open now.");
};

socket.onmessage = async (event) => {
    try {
        const data = JSON.parse(event.data);
        console.log("Received message:", data);

        if (data.type === "offer") {
            console.log("Received offer");
            await handleOffer(data.data);
        } else if (data.type === "answer") {
            console.log("Received answer");
            await handleAnswer(data.data);
        } else if (data.type === "ice") {
            console.log("Received ICE candidate");
            await handleIceCandidate(data.data);
        } else if (data.type === "new_user") {
            console.log("A new user has joined the room:", data.data.user_id);
            if (myPeerConnection) {
                // 새로운 사용자가 들어왔을 때 offer 생성하여 전송
                console.log("Creating offer as a new user joined");
                const offer = await myPeerConnection.createOffer();
                await myPeerConnection.setLocalDescription(offer);
                socket.send(JSON.stringify({ type: "offer", data: offer, roomName}));
                console.log("Sent offer:", offer);
            }
        }
    } catch (e) {
        console.error("Failed to parse message:", e, "Message data:", event.data);
    }
};


async function handleOffer(offer) {
    if (!myPeerConnection) {
        makeConnection();
    }
    await myPeerConnection.setRemoteDescription(new RTCSessionDescription(offer));
    const answer = await myPeerConnection.createAnswer();
    await myPeerConnection.setLocalDescription(answer);
    socket.send(JSON.stringify({ type: "answer", data: answer, roomName }));
    console.log("Sent answer:", answer);
}

async function handleAnswer(answer) {
    await myPeerConnection.setRemoteDescription(new RTCSessionDescription(answer));
    console.log("Set remote description with answer");
}

async function handleIceCandidate(candidate) {
    try {
        await myPeerConnection.addIceCandidate(new RTCIceCandidate(candidate));
        console.log("Added ICE candidate:", candidate);
    } catch (e) {
        console.error("Error adding received ice candidate", e);
    }
}

function handleIce(event) {
    if (event.candidate) {
        socket.send(JSON.stringify({
            type: "ice",
            data: event.candidate,
            roomName
        }));
        console.log("Sent ICE candidate:", event.candidate);
    }
}

function makeConnection() {
    myPeerConnection = new RTCPeerConnection({
        iceServers: [
            {
                urls: [
                    "stun:stun.l..com:19302",
                    "stun:stun1.l.google.com:19302",
                    "stun:stun2.l.google.com:19302",
                    "stun:stun3.l.google.com:19302",
                    "stun:stun4.l.google.com:19302",
                ],
            },
        ],
    });

    myPeerConnection.addEventListener("icecandidate", handleIce);
    myPeerConnection.addEventListener("track", handleTrackEvent);

    myStream.getTracks().forEach(track => myPeerConnection.addTrack(track, myStream)); //.getTracks() https 통신
}

function handleTrackEvent(event) {
    console.log("Track event - stream added:", event.streams);
    const peerStream = event.streams[0];
    peerFace.srcObject = peerStream;
}
