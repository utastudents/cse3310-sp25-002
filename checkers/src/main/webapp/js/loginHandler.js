
// Username Validation
var uInput = document.getElementById("username");
var letter = document.getElementById("letter");
var num = document.getElementById("num");
var special = document.getElementById("special");
var length = document.getElementById("length");


// User Clicks in input field
uInput.onfocus = function() {
  document.getElementById("login_message").style.display = "block";
}

// User Clicks outside input field
uInput.onblur = function() {
  document.getElementById("login_message").style.display = "none";
}


//User Starts Typing
uInput.onkeyup = function() {

  // Form Validation for Username

  //Letter
  var Letter = /[A-Za-z]/g;
  if(uInput.value.match(Letter)) {  
    letter.classList.remove("login_invalid");
    letter.classList.add("login_valid");
  } else {
    letter.classList.remove("login_valid");
    letter.classList.add("login_invalid");
  }

  // Numbers
  var numbers = /[0-9]/g;
  if(uInput.value.match(numbers)) {  
    num.classList.remove("login_invalid");
    num.classList.add("login_valid");
  } else {
    num.classList.remove("login_valid");
    num.classList.add("login_invalid");
  }

  // Special Characters
  var specialChars = /[!@#$%^&*]/g;
  if(uInput.value.match(specialChars)) {  
    special.classList.remove("login_invalid");
    special.classList.add("login_valid");
  } else {
    special.classList.remove("login_valid");
    special.classList.add("login_invalid");
  }
  
  // Length
  if(uInput.value.length >= 8) {
    length.classList.remove("login_invalid");
    length.classList.add("login_valid");
  } else {
    length.classList.remove("login_valid");
    length.classList.add("login_invalid");
  }
}

function joinGame() 
{
  const username = document.getElementById('username').value;
  const pattern = /^(?=.*\d)(?=.*[!@#$%^&*])[A-Za-z\d!@#$%^&*]{8,}$/;
  if (!username) 
    {
    alert('Please enter a username.');
  } 
  else if (!pattern.test(username)) 
    {
    alert('Username must be atleast: 8 characters long, contain one digit and one special character.');
    } 
  else 
  {
    sendUsername(username);
    
  }
}

function sendUsername()
{
  //Take username and remove whitespace
    const username = document.getElementById("username").value.trim();
    const errorDiv = document.getElementById("username_error");
  
  //if username field is empty, show an error and stop the function.
    if(!username) 
    {
      errorDiv.innerText = "Please enter a username";
      errorDiv.style.display = "block";
      return; //exit function since the usename is invalid.
    }
    const userData = 
  {
    type: "join", 
    playerName: username
  };

  //Sending name through WebSocket

  if (connection.readyState === WebSocket.OPEN) {
    connection.send(JSON.stringify(userData)); //Sending JSON
    console.log("sending "+JSON.stringify(userData));
  } else {
    errorDiv.innerText = "Connection to server not available.";
    errorDiv.style.display = "block";
  }
}
        
function receiveFromPageManager(msg) {
  console.log("the message is "+msg);

  // Handling backend message structure
  if (msg.Status === "Success") 
  {
      console.log("success on the username");
    /*If the username is valid and user is added in DB, 
    hide the current login section*/

    document.getElementById("new_account").style.display = "none";
    document.getElementById("game_lobby").style.display = "initial";
    document.getElementById("join_game").style.display = "none";
    document.getElementById("game_display").style.display = "none";
    document.getElementById("summary").style.display = "none";

 } 
 else if (msg.Status === "Error") //If username exists or input is invalid.
 {
   console.log("Username is already taken or invalid:", msg.Message);
    alert(msg.Message || "Username not accepted.");
 } 
}
