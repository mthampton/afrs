'use strict';

navigator.getUserMedia = navigator.getUserMedia || navigator.webkitGetUserMedia || navigator.mozGetUserMedia;

var constraints = {audio: false, video: true};
var video = document.getElementById("live");
var canvas = document.getElementById("canvas");
var ctx = canvas.getContext("2d");

function successCallback(stream) {
	window.stream = stream; // stream available to console
	if (window.URL) {
		video.src = window.URL.createObjectURL(stream);
	} else {
		video.src = stream;
	}
}

function errorCallback(error){
	console.log('navigator.getUserMedia error: ', error);
}

navigator.getUserMedia(constraints, successCallback, errorCallback);

function dataURItoBlob1(dataURI) {
    var binary = atob(dataURI.split(',')[1]);
    var array = [];
    for(var i = 0; i < binary.length; i++) {
        array.push(binary.charCodeAt(i));
    }
    return new Blob([new Uint8Array(array)], {type: 'image/png'});
}


function dataURItoBlob2(dataURI) {
    // convert base64/URLEncoded data component to raw binary data held in a string
    var byteString;
    if (dataURI.split(',')[0].indexOf('base64') >= 0)
        byteString = atob(dataURI.split(',')[1]);
    else
        byteString = unescape(dataURI.split(',')[1]);

    // separate out the mime component
    var mimeString = dataURI.split(',')[0].split(':')[1].split(';')[0];

    // write the bytes of the string to a typed array
    var ia = new Uint8Array(byteString.length);
    for (var i = 0; i < byteString.length; i++) {
        ia[i] = byteString.charCodeAt(i);
    }

    return new Blob([ia], {type:mimeString});
}

function dataURItoBlob3(dataURI) {
    var byteString = atob(dataURI.split(',')[1]);
    var ab = new ArrayBuffer(byteString.length);
    var ia = new Uint8Array(ab);
    for (var i = 0; i < byteString.length; i++) {
        ia[i] = byteString.charCodeAt(i);
    }
    return new Blob([ab], { type: 'image/png' });
}


// WebSocket stuff
var ws = null;

function setConnected(connected) {
    document.getElementById('connect').disabled = connected;
    document.getElementById('disconnect').disabled = !connected;
    document.getElementById('echo').disabled = !connected;
}

function connect() {
    var targetServer = document.getElementById('targetServer').value;
    if (targetServer == '') {
        alert('Please select server side connection implementation.');
        return;
    }
    if ('WebSocket' in window) {
        ws = new WebSocket(targetServer);
    } else if ('MozWebSocket' in window) {
        ws = new MozWebSocket(targetServer);
    } else {
        alert('WebSocket is not supported by this browser.');
        return;
    }
    ws.onopen = function () {
        setConnected(true);
        log('Info: WebSocket connection opened.');
    };
    ws.onmessage = function (event) {
        log('Received the processed message: ' + event.data);
        //log('Received a message: ');
	var targetImage = document.getElementById("targetImage");
        log('Set the targetImage: ' + targetImage);
	//var url = window.webkitURL.createObjectURL(event.data);
	var url = URL.createObjectURL(event.data);
	log('The url is: ' + url);
	targetImage.onload = function() {
		window.webkitURL.revokeObjectURL(url);
	};
	targetImage.src = url;

    };
    ws.onclose = function (event) {
        setConnected(false);
        log('Info: WebSocket connection closed, Code: ' + event.code + (event.reason == "" ? "" : ", Reason: " + event.reason));
    };
}

function disconnect() {
    if (ws != null) {
        ws.close();
        ws = null;
    }
    setConnected(false);
}

function echo() {
    if (ws != null) {
        var message = document.getElementById('message').value;
        log('Sent: ' + message);
        ws.send(message);
    } else {
        alert('WebSocket connection not established, please connect.');
    }
}

function sendBlob(blob) {
    if (ws != null) {
        log('Sending the image for processing.');
        log('ws.bufferedAmount: ' + ws.bufferedAmount);
        ws.send(blob);
        log('ws.bufferedAmount: ' + ws.bufferedAmount);
    } else {
        alert('WebSocket connection not established, please connect.');
    }
}

function updateTarget(target) {
    if (window.location.protocol == 'http:') {
        document.getElementById('target').value = 'ws://' + window.location.host + target;
    } else {
        document.getElementById('target').value = 'wss://' + window.location.host + target;
    }
}

function captureAndSend() {
	ctx.drawImage(video, 0, 0, 320, 240);
	var data = canvas.toDataURL('image/png', 1.0);
	//log('The data that was captured: ' + data);
	var newblob = dataURItoBlob2(data);
	//log('The blob: ' + newblob);
	if (ws != null) {
		log('Blob size: ' + newblob.size);
	    sendBlob(newblob);
	}
}

function log(message) {
    var console = document.getElementById('console');
    var p = document.createElement('p');
    p.style.wordWrap = 'break-word';
    p.appendChild(document.createTextNode(message));
    console.appendChild(p);
    while (console.childNodes.length > 25) {
        console.removeChild(console.firstChild);
    }
    console.scrollTop = console.scrollHeight;
}

document.addEventListener("DOMContentLoaded", function() {
    // Remove elements with "noscript" class - <noscript> is not allowed in XHTML
    var noscripts = document.getElementsByClassName("noscript");
    for (var i = 0; i < noscripts.length; i++) {
        noscripts[i].parentNode.removeChild(noscripts[i]);
    }
}, false);

connect();

//Need to put this on an onclick ....
//var timer = setInterval(
//        function () {
//           ctx.drawImage(video, 0, 0, 320, 240);
//            var data = canvas.toDataURL('image/jpeg', 1.0);
//            var newblob = dataURItoBlob(data);
//	    if (ws != null) {
//	    sendBlob(newblob);
//	   }
//        }, 250);
