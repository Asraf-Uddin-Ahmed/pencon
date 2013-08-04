/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pencon;

import java.util.ArrayList;
import java.util.Comparator;

/**
 *
 * @author RATUL
 */
public class CustomComparator implements Comparator< ArrayList<String> > {
    @Override
    public int compare(ArrayList<String> teamHistory1, ArrayList<String> teamHistory2) {
        int comp = 0;

        final String totalAc1 = teamHistory1.get(1);
        final String totalAc2 = teamHistory2.get(1);
        comp = totalAc2.compareTo(totalAc1);
        if( comp!=0 )
                return comp;

        final String penaltyTime1 = teamHistory1.get(2);
        final String penaltyTime2 = teamHistory2.get(2);
        comp = penaltyTime1.compareTo(penaltyTime2);
        if( comp!=0 )
                return comp;

        final String teamName1 = teamHistory1.get(0);
        final String teamName2 = teamHistory2.get(0);
        return teamName1.compareTo(teamName2);
    }
}


