
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
            const username = document.getElementById("username").value.trim();
            const errorDiv = document.getElementById("username_error");
        if(!username)
            {
                errorDiv.innerText = "Please enter a username."
                errorDiv.style.display = "block"
                return;
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
        }
       


        function recieveFromPageManager(msg)
        {


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
        
        }
       