const ORDER_SERVICE_URL = 'http://localhost:8083/ws'; // The endpoint from WebSocketConfig
const USER_JWT_TOKEN = 'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxMSIsInJvbGUiOiJST0xFX1JFU1RBVVJBTlRfQURNSU4iLCJpYXQiOjE3NjA1OTI0OTUsImV4cCI6MTc2MDY3ODg5NX0.yY2JRQZsBa55jQzvvzpOP0lKO9ZeNF24yLy62SAH595PBOJLu9lpEIzM2X8jlH3dnmsSaVrejv2A0S0BnHB-HA';

const statusDiv = document.getElementById('status-updates');
let stompClient = null;

function connect() {
    // Create a new SockJS connection
    const socket = new SockJS(ORDER_SERVICE_URL);
    stompClient = Stomp.over(socket);

    // **CRITICAL**: Pass the JWT for authentication
    // Spring Security will use this header to identify the user principal.
    const headers = {
        'Authorization': `Bearer ${USER_JWT_TOKEN}`
    };

    stompClient.connect(headers, function (frame) {
        console.log('Connected: ' + frame);
        statusDiv.innerHTML += '<p>Connected to order tracking!</p>';

        // Subscribe to the user-specific queue
        // The destination `/user/queue/order-updates` resolves on the server
        // to a unique destination for this user's session.
        stompClient.subscribe('/user/queue/order-updates', function (message) {
            const update = JSON.parse(message.body);
            showStatusUpdate(update);
        });
    }, function(error) {
        console.error('STOMP error: ' + error);
        statusDiv.innerHTML += '<p>Connection error!</p>';
    });
}

function showStatusUpdate(update) {
    const p = document.createElement('p');
    p.textContent = `[Order #${update.orderId}] Status changed to: ${update.status} - ${update.description}`;
    statusDiv.appendChild(p);
}

// Start the connection when the script loads
connect();