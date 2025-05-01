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
  
document.addEventListener('DOMContentLoaded', () => {
  const searchInput = document.querySelector('.search');
  const listEl = document.querySelector('.player-list');
  const statsEl = document.getElementById('playerStats');

  const currentUser = sessionStorage.getItem('username');

  async function loadPlayerStats() {
    if (!currentUser) {
      statsEl.innerText = 'Not logged in';
      return;
    }
    try {
      const res = await fetch(`/api/user/${encodeURIComponent(currentUser)}`);
      if (!res.ok) throw new Error(res.statusText);
      const u = await res.json(); 
      statsEl.innerHTML = `
        <p>Name: ${u.username}</p>
        <p>Wins: ${u.wins}</p>
        <p>Losses: ${u.losses}</p>
        <p>Time Played: ${u.timePlayed}</p>
      `;
    } catch (err) {
      console.error('loadPlayerStats()', err);
      statsEl.innerText = 'Stats unavailable';
    }
  }

  let leaderboard = [];
  async function loadLeaderboard() {
    try {
      const res = await fetch('/api/leaderboard');
      if (!res.ok) throw new Error(res.statusText);
      const players = await res.json(); 

      players.sort((a, b) => {
        const rA = a.losses === 0 ? a.wins : a.wins / a.losses;
        const rB = b.losses === 0 ? b.wins : b.wins / b.losses;
        return rB - rA;
      });

      leaderboard = players.slice(0, 100);
      renderList(leaderboard);
    } catch (err) {
      console.error('loadLeaderboard()', err);
      listEl.innerHTML = '<li class="error">Failed to load leaderboard</li>';
    }
  }
  function renderList(arr) {
    listEl.innerHTML = '';
    arr.forEach((p, i) => {
      const li = document.createElement('li');
      li.innerHTML = `
        <p class="playerRank">${i + 1}</p>
        <p class="playerName">${p.username}</p>
        <p class="playerWin">${p.wins}</p>
        <p class="playerLoss">${p.losses}</p>
        <p class="playerTime">${p.timePlayed}</p>
      `;
      listEl.appendChild(li);
    });
  }

  searchInput.addEventListener('input', () => {
    const q = searchInput.value.trim().toLowerCase();
    if (!q) {
      renderList(leaderboard);
    } else {
      renderList(
        leaderboard.filter(p =>
          p.username.toLowerCase().includes(q)
        )
      );
    }
  });

  loadPlayerStats();
  loadLeaderboard();
});
