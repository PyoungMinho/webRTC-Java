// const socket = new WebSocket("ws://localhost:8080/ws");
//
// const myFace = document.getElementById('myFace');
// const muteBtn = document.getElementById("mute");
// const cameraBtn = document.getElementById("camera");
// const cameraSelect = document.getElementById("cameras");
// const call = document.getElementById("call");
//
// call.hidden = true;
//
// let myStream;
// let muted = false;
// let cameraOff = false;
// let roomName;
// let myPeerConnection;
// let myDataChannel;
//
// socket.onopen = () => {
//     console.log("WebSocket is open now.");
// };
//
// socket.onerror = (error) => {
//     console.error("WebSocket error observed:", error);
// };
//
// function sendMessage(message) {
//     if (socket.readyState === WebSocket.OPEN) {
//         socket.send(message);
//     } else {
//         console.error("WebSocket is not open. Current state: " + socket.readyState);
//     }
// }
//
// async function getCameras() {
//     try {
//         const devices = await navigator.mediaDevices.enumerateDevices();
//         const cameras = devices.filter((device) => device.kind === "videoinput");
//         const currentCamera = myStream.getVideoTracks()[0];
//         cameras.forEach(camera => {
//             const option = document.createElement("option");
//             option.value = camera.deviceId;
//             option.innerText = camera.label;
//             if (currentCamera.label === camera.label) {
//                 option.selected = true;
//             }
//             cameraSelect.appendChild(option);
//         });
//     } catch (e) {
//         console.log(e);
//     }
// }
//
// async function getMedia(deviceId) {
//     const initialConstrains = {
//         audio: true,
//         video: { facingMode: "user" },
//     };
//     const cameraConstraints = {
//         audio: true,
//         video: { deviceId: { exact: deviceId } },
//     };
//
//     try {
//         myStream = await navigator.mediaDevices.getUserMedia(
//             deviceId ? cameraConstraints : initialConstrains
//         );
//         myFace.srcObject = myStream;
//         if (!deviceId) {
//             await getCameras();
//         }
//     } catch (e) {
//         console.log(e);
//     }
// }
//
// getMedia();
//
// function handleMuteClick() {
//     myStream.getAudioTracks().forEach((track) => (track.enabled = !track.enabled));
//     muteBtn.innerText = muted ? "Mute" : "Unmute";
//     muted = !muted;
// }
//
// function handleCameraClick() {
//     myStream.getVideoTracks().forEach((track) => (track.enabled = !track.enabled));
//     cameraBtn.innerText = cameraOff ? "Turn Camera On" : "Turn Camera Off";
//     cameraOff = !cameraOff;
// }
//
// async function handleCameraChange() {
//     await getMedia(cameraSelect.value);
//     if (myPeerConnection) {
//         const videoTrack = myStream.getVideoTracks()[0];
//         const videoSender = myPeerConnection
//             .getSenders()
//             .find((sender) => sender.track.kind === "video");
//         if (videoSender) {
//             videoSender.replaceTrack(videoTrack);
//         }
//     }
// }
//
// muteBtn.addEventListener("click", handleMuteClick);
// cameraBtn.addEventListener("click", handleCameraClick);
// cameraSelect.addEventListener("input", handleCameraChange);
//
// const welcomeForm = document.querySelector("form");
// const welcome = document.getElementById("welcome");
//
// async function initCall() {
//     welcome.hidden = true;
//     call.hidden = false;
//     await getMedia();
//     makeConnection();
// }
//
// async function handleWelcomeSubmit(event) {
//     event.preventDefault();
//     const input = document.querySelector("input");
//     roomName = input.value;
//     input.value = "";
//     await initCall();
//     sendMessage(JSON.stringify({ type: "join_room", roomName: roomName }));
// }
//
// welcomeForm.addEventListener("submit", handleWelcomeSubmit);
//
// socket.onmessage = async (event) => {
//     const data = JSON.parse(event.data);
//     try {
//         if (data.type === "welcome") {
//             myDataChannel = myPeerConnection.createDataChannel("chat");
//             myDataChannel.addEventListener("message", (event) => console.log(event.data));
//             const offer = await myPeerConnection.createOffer();
//             await myPeerConnection.setLocalDescription(offer);
//             sendMessage(JSON.stringify({ type: "offer", offer: offer, roomName: roomName }));
//         } else if (data.type === "offer") {
//             if (myPeerConnection.signalingState !== "stable") {
//                 console.error("Peer connection is not in a stable state to set remote offer.");
//                 return;
//             }
//             myPeerConnection.addEventListener("datachannel", (event) => {
//                 myDataChannel = event.channel;
//                 myDataChannel.addEventListener("message", (event) => console.log(event.data));
//             });
//             await myPeerConnection.setRemoteDescription(new RTCSessionDescription(data.offer));
//             const answer = await myPeerConnection.createAnswer();
//             await myPeerConnection.setLocalDescription(answer);
//             sendMessage(JSON.stringify({ type: "answer", answer: answer, roomName: roomName }));
//         } else if (data.type === "answer") {
//             if (myPeerConnection.signalingState !== "have-local-offer" && myPeerConnection.signalingState !== "have-remote-offer") {
//                 console.error("Peer connection is not in a state to set remote answer.");
//                 return;
//             }
//             await myPeerConnection.setRemoteDescription(new RTCSessionDescription(data.answer));
//         } else if (data.type === "ice") {
//             if (data.candidate) {
//                 if (myPeerConnection.remoteDescription) {
//                     await myPeerConnection.addIceCandidate(new RTCIceCandidate(data.candidate));
//                 } else {
//                     console.error("Remote description is not set. Cannot add ICE candidate.");
//                 }
//             }
//         }
//     } catch (error) {
//         console.error("Error processing WebSocket message:", error);
//     }
// };
//
// // RTC Code
// function makeConnection() {
//     myPeerConnection = new RTCPeerConnection({
//         iceServers: [
//             {
//                 urls: [
//                     "stun:stun.l.google.com:19302",
//                     "stun:stun1.l.google.com:19302",
//                     "stun:stun2.l.google.com:19302",
//                     "stun:stun3.l.google.com:19302",
//                     "stun:stun4.l.google.com:19302",
//                 ],
//             },
//         ],
//     });
//
//     myPeerConnection.addEventListener("icecandidate", handleIce);
//     myPeerConnection.addEventListener("track", handleAddStream); // Updated to use 'track' event
//     myStream
//         .getTracks()
//         .forEach((track) => myPeerConnection.addTrack(track, myStream));
// }
//
// function handleIce(event) {
//     if (event.candidate) {
//         sendMessage(JSON.stringify({ type: "ice", candidate: event.candidate, roomName: roomName }));
//     }
// }
//
// function handleAddStream(event) {
//     const peerFace = document.getElementById("peerFace");
//     peerFace.srcObject = event.streams[0]; // Updated to use 'streams'
// }
const socket = new WebSocket("ws://localhost:8080/ws");

const myFace = document.getElementById('myFace');
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
let myDataChannel;

socket.onopen = () => {
    console.log("WebSocket is open now.");
};

socket.onerror = (error) => {
    console.error("WebSocket error observed:", error);
};

function sendMessage(message) {
    if (socket.readyState === WebSocket.OPEN) {
        socket.send(message);
    } else {
        console.error("WebSocket is not open. Current state: " + socket.readyState);
    }
}

async function getCameras() {
    try {
        const devices = await navigator.mediaDevices.enumerateDevices();
        const cameras = devices.filter((device) => device.kind === "videoinput");
        const currentCamera = myStream ? myStream.getVideoTracks()[0] : null;
        cameras.forEach(camera => {
            const option = document.createElement("option");
            option.value = camera.deviceId;
            option.innerText = camera.label;
            if (currentCamera && currentCamera.label === camera.label) {
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
        video: { facingMode: "user" },
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
    myStream.getAudioTracks().forEach((track) => (track.enabled = !track.enabled));
    muteBtn.innerText = muted ? "Mute" : "Unmute";
    muted = !muted;
}

function handleCameraClick() {
    myStream.getVideoTracks().forEach((track) => (track.enabled = !track.enabled));
    cameraBtn.innerText = cameraOff ? "Turn Camera On" : "Turn Camera Off";
    cameraOff = !cameraOff;
}

async function handleCameraChange() {
    await getMedia(cameraSelect.value);
    if (myPeerConnection) {
        const videoTrack = myStream.getVideoTracks()[0];
        const videoSender = myPeerConnection
            .getSenders()
            .find((sender) => sender.track.kind === "video");
        if (videoSender) {
            videoSender.replaceTrack(videoTrack);
        }
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
    input.value = "";
    await initCall();
    sendMessage(JSON.stringify({ type: "join_room", roomName: roomName }));
}

welcomeForm.addEventListener("submit", handleWelcomeSubmit);

socket.onmessage = async (event) => {
    const data = JSON.parse(event.data);
    try {
        if (data.type === "welcome") {
            myDataChannel = myPeerConnection.createDataChannel("chat");
            myDataChannel.addEventListener("message", (event) => console.log(event.data));
            const offer = await myPeerConnection.createOffer();
            await myPeerConnection.setLocalDescription(offer);
            sendMessage(JSON.stringify({ type: "offer", offer: offer, roomName: roomName }));
        } else if (data.type === "offer") {
            if (myPeerConnection.signalingState !== "stable") {
                console.error("Peer connection is not in a stable state to set remote offer.");
                return;
            }
            myPeerConnection.addEventListener("datachannel", (event) => {
                myDataChannel = event.channel;
                myDataChannel.addEventListener("message", (event) => console.log(event.data));
            });
            await myPeerConnection.setRemoteDescription(new RTCSessionDescription(data.offer));
            const answer = await myPeerConnection.createAnswer();
            await myPeerConnection.setLocalDescription(answer);
            sendMessage(JSON.stringify({ type: "answer", answer: answer, roomName: roomName }));
        } else if (data.type === "answer") {
            if (myPeerConnection.signalingState !== "have-local-offer" && myPeerConnection.signalingState !== "have-remote-offer") {
                console.error("Peer connection is not in a state to set remote answer.");
                return;
            }
            await myPeerConnection.setRemoteDescription(new RTCSessionDescription(data.answer));
        } else if (data.type === "ice") {
            if (data.candidate) {
                if (myPeerConnection.remoteDescription) {
                    await myPeerConnection.addIceCandidate(new RTCIceCandidate(data.candidate));
                } else {
                    console.error("Remote description is not set. Cannot add ICE candidate.");
                }
            }
        }
    } catch (error) {
        console.error("Error processing WebSocket message:", error);
    }
};

// RTC Code
function makeConnection() {
    myPeerConnection = new RTCPeerConnection({
        iceServers: [
            {
                urls: [
                    "stun:stun.l.google.com:19302",
                    "stun:stun1.l.google.com:19302",
                    "stun:stun2.l.google.com:19302",
                    "stun:stun3.l.google.com:19302",
                    "stun:stun4.l.google.com:19302",
                ],
            },
        ],
    });

    myPeerConnection.addEventListener("icecandidate", handleIce);
    myPeerConnection.addEventListener("track", handleAddStream);
    myStream
        .getTracks()
        .forEach((track) => myPeerConnection.addTrack(track, myStream));
}

function handleIce(event) {
    if (event.candidate) {
        sendMessage(JSON.stringify({ type: "ice", candidate: event.candidate, roomName: roomName }));
    }
}

function handleAddStream(event) {
    const peerFace = document.getElementById("peerFace");
    peerFace.srcObject = event.streams[0];
}
