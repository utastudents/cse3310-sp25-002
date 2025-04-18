// function callPlayerStats() {
//     call java function and return current player stats
// return playerStats
// }

// function showPlayerStats() {
// const result = callPlayerStats(); 
//   could implement over several js functions and format in result variable or format callPlayerStats return value 
// document.getElementById("playerStats").innerText = result;
// }

// example of what implementation should look like

// async function loadLeaderboard() {
//     // Get player data from server
//     const res = await fetch('/api/players');
//     const players = await res.json();
  
//     // Sort by win/loss ratio
//     players.sort((a, b) => {
//       const ratioA = a.losses === 0 ? a.wins : a.wins / a.losses;
//       const ratioB = b.losses === 0 ? b.wins : b.wins / b.losses;
//       return ratioB - ratioA;
//     });
  
//     // Render to DOM
//     const list = document.querySelector('.player-list');
//     list.innerHTML = '';
  
//     players.forEach((player, i) => {
//       const li = document.createElement('li');
//       li.innerHTML = `
//         <p class="playerRank">${i + 1}</p>
//         <p class="playerName">${player.name}</p>
//         <p class="playerWin">${player.wins}</p>
//         <p class="playerLoss">${player.losses}</p>
//         <p class="playerTime">${player.time}</p>
//       `;
//       list.appendChild(li);
//     });
//   }
  
//   document.addEventListener('DOMContentLoaded', loadLeaderboard);
  