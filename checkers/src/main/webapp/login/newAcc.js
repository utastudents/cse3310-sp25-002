
// Username Validation
var uInput = document.getElementById("username");
var letter = document.getElementById("letter");
var num = document.getElementById("num");
var special = document.getElementById("special");
var length = document.getElementById("length");
var space = document.getElementById("space");

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

  //No spaces
  var hasSpace = /\s/;
  if (!hasSpace.test(uInput.value)) {
    space.classList.remove("login_invalid");
    space.classList.add("login_valid");
  } else {
    space.classList.remove("login_valid");
    space.classList.add("login_invalid");
  }
}



    function joinGame() 
    {
      const username = document.getElementById('username').value;
      const pattern = /^(?=.*\d)(?=.*[!@#$%^&*])(?=.*[A-Za-z])(?!.*\s).{8,}$/;
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
                playerName: username
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
