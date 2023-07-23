package io.github.karstenspang.subsets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class CombinatorialTest {
    @Test
    @DisplayName("Bad arguments throw IllegalArgumentException")
    void barArgs(){
        assertThrows(IllegalArgumentException.class,()->Combinatorial.comb(-1,0),"m<0");
        assertThrows(IllegalArgumentException.class,()->Combinatorial.comb(0,1),"n>m");
        assertThrows(IllegalArgumentException.class,()->Combinatorial.comb(0,-1),"n<0");
    }
    
    @ParameterizedTest
    @ValueSource(ints={0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16})
    @DisplayName("Result matches values computed by counting subsets of different sizes")
    void subsets(int m){
        Set<Integer> baseSet=new HashSet<>(m);
        for (int i=0;i<m;i++){
            baseSet.add(i);
        }
        List<Integer> expected=new ArrayList<>(m+1);
        expected.addAll(new Subsets<Integer>(baseSet).stream().collect(Collectors.toMap(Set::size,s->Integer.valueOf(1),Integer::sum,TreeMap::new)).values());
        List<Integer> result=new ArrayList<>(m+1);
        for (int n=0;n<=m;n++){
            result.add(Combinatorial.comb(m,n).intValue());
        }
        assertEquals(expected,result);
    }
}
