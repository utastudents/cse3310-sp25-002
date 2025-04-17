package cse3310.uta.PairUpTests;

import org.junit.Test;
import uta.cse3310.PairUp.*;
import static org.junit.Assert.*;

public class Test3 
{
    @Test
    public void TestAddPlayer()
    {
        PairUp pairUp = new PairUp();

        pairUp.AddPlayer(171328890285L,451, "Alice",false,9);
    
        assertEquals(true, pairUp.searchPlayer(451));
        
        pairUp.clearPlayers();

    }
}