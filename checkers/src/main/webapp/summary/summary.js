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
/*
document.addEventListener('DOMContentLoaded', () => {
  const searchInput = document.querySelector('.search');
  const listEl = document.querySelector('.player-list');
  const statsEl = document.getElementById('playerStats');

  const currentUser = sessionStorage.getItem('username');

  async function loadPlayerStats() {
    if (!currentUser) {
    //  statsEl.innerText = 'Not logged in';
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
        <p>Rank: ${u.rank || 'N/A'}</p>
      `;
    } catch (err) {
      console.error('loadPlayerStats()', err);
    //  statsEl.innerText = 'Stats unavailable';
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
     // listEl.innerHTML = '<li class="error">Failed to load leaderboard</li>';
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
        <p class="playerTime">—</p> <!-- You can replace this with actual time if available -->
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

*/
