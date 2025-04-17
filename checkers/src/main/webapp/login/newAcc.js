
// Username Validation
var uInput = document.getElementById("username");
var letter = document.getElementById("letter");
var num = document.getElementById("num");
var special = document.getElementById("special");
var length = document.getElementById("length");


// User Clicks in input field
uInput.onfocus = function() {
  document.getElementById("message").style.display = "block";
}

// User Clicks outside input field
uInput.onblur = function() {
  document.getElementById("message").style.display = "none";
}


//User Starts Typing
uInput.onkeyup = function() {

  // Form Validation for Username

  //Letter
  var Letter = /[A-Za-z]/g;
  if(uInput.value.match(Letter)) {  
    letter.classList.remove("invalid");
    letter.classList.add("valid");
  } else {
    letter.classList.remove("valid");
    letter.classList.add("invalid");
  }

  // Numbers
  var numbers = /[0-9]/g;
  if(uInput.value.match(numbers)) {  
    num.classList.remove("invalid");
    num.classList.add("valid");
  } else {
    num.classList.remove("valid");
    num.classList.add("invalid");
  }

  // Special Characters
  var specialChars = /[!@#$%^&*]/g;
  if(uInput.value.match(specialChars)) {  
    special.classList.remove("invalid");
    special.classList.add("valid");
  } else {
    special.classList.remove("valid");
    special.classList.add("invalid");
  }
  
  // Length
  if(uInput.value.length >= 8) {
    length.classList.remove("invalid");
    length.classList.add("valid");
  } else {
    length.classList.remove("valid");
    length.classList.add("invalid");
  }
}



    function joinGame() 
    {
      const username = document.getElementById('username').value;
      const pattern = /^(?=.*\d)(?=.*[!@#$%^&*])[A-Za-z\d!@#$%^&*]{8,}$/;
      if (!username) {
        alert('Please enter a username.');
      } else if (!pattern.test(username)) {
        alert('Username must be atleast: 8 characters long, contain one digit and one special character.');
      } else {
        alert('Joining Game');
      }
    }
            
            function sendUsername()
            {
                const username = document.getElementById("username").value.trim();
                const errorDiv = document.getElementById("username_error");
            if(!username)
                {
                    errorDiv.innerText = "Please enter a username."
                    errorDiv.style.display = "block"
                    return;
                }
            }
            const userData = 
            {
                type: "join",
                username: username
            }
            // Sending data through WebSocket
            if(connection === WebSocket.OPEN)
            {
                connection.send(JSON.stringify(userData));
            }
            else
            {
                errorDiv.innerText = "Connection to server not available.";
                errorDiv.style.display = "block";
            }


            function recieveFromPageManager(msg)
            {
                // We get a message.
            }
            if(msg.type ==="username_status")
            {
                /* If username is valid (not taken) we hide the current (new_account)
                and show the next, possibly (join_game) section */
                if (msg.accepted === true)  
                {
                    document.getElementById("new_account").style.display = "none"
                    document.getElementById("join_game").style.display = "block"
                }
                else
                {
                    console.log("Username is already taken"); //If not ask for another username.

                }

            }
            else
            {
                console.log("Unkonown message type", msg.type);
            }
