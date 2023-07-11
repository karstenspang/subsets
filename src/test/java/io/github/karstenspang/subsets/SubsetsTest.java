package io.github.karstenspang.subsets;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import org.junit.jupiter.params.provider.MethodSource;

class SubsetsTest {

    private enum TestEnum{A,B,C};
    
    private static List<Set<TestEnum>> fullSortedList=Arrays.asList(
        EnumSet.noneOf(TestEnum.class),
        EnumSet.of(TestEnum.A),
        EnumSet.of(TestEnum.B),
        EnumSet.of(TestEnum.A,TestEnum.B),
        EnumSet.of(TestEnum.C),
        EnumSet.of(TestEnum.A,TestEnum.C),
        EnumSet.of(TestEnum.B,TestEnum.C),
        EnumSet.of(TestEnum.A,TestEnum.B,TestEnum.C)
    );

    @ParameterizedTest
    @MethodSource("setProviderProvider")
    void testEmpty(Supplier<Set<TestEnum>> setProvider,Class<?> setClass,Comparator<? extends TestEnum> comparator){
        Set<TestEnum> input=setProvider.get();
        Subsets<TestEnum> subsets=new Subsets<>(input);
        List<Set<TestEnum>> result=subsets.asList();
        assertEquals(1,result.size(),"size");
        Set<TestEnum> empty=result.get(0);
        assertEquals(Collections.emptySet(),empty,"empty");
        assertInstanceOf(setClass,empty,"class");
        if (setClass==SortedSet.class){
            SortedSet<TestEnum> sortedEmpty=(SortedSet<TestEnum>)empty;
            assertEquals(comparator,sortedEmpty.comparator(),"comparator");
        }
    }
    
    @ParameterizedTest
    @MethodSource("setProviderProvider")
    void testFull(Supplier<Set<TestEnum>> setProvider,Class<?> setClass,Comparator<? extends TestEnum> comparator){
        Set<TestEnum> input=setProvider.get();
        input.addAll(EnumSet.allOf(TestEnum.class));
        Subsets<TestEnum> subsets=new Subsets<>(input);
        List<Set<TestEnum>> result=subsets.asList();
        assertEquals(8,result.size(),"size");
        if (setClass==Set.class){
            assertTrue(result.containsAll(fullSortedList),"contains");
        }
        else{
            assertEquals(fullSortedList,result,"sorted");
        }
        Set<TestEnum> empty=result.get(0);
        assertEquals(Collections.emptySet(),empty,"empty");
        assertInstanceOf(setClass,empty,"class");
        if (setClass==SortedSet.class){
            SortedSet<TestEnum> sortedEmpty=(SortedSet<TestEnum>)empty;
            assertEquals(comparator,sortedEmpty.comparator(),"comparator");
        }
    }
    
    static Stream<Arguments> setProviderProvider(){
        return Stream.of(
            arguments((Supplier<Set<TestEnum>>)(HashSet<TestEnum>::new),Set.class,null),
            arguments((Supplier<Set<TestEnum>>)(TreeSet<TestEnum>::new),SortedSet.class,null),
            arguments((Supplier<Set<TestEnum>>)(()->new TreeSet<TestEnum>(Comparator.naturalOrder())),SortedSet.class,Comparator.naturalOrder()),
            arguments((Supplier<Set<TestEnum>>)(()->EnumSet.noneOf(TestEnum.class)),EnumSet.class,null)
        );
    }
    
    @Test
    void setTooBig(){
        HashSet<Integer> big=new HashSet<>(63);
        for (int i=0;i<63;i++) big.add(i);
        assertThrows(IllegalArgumentException.class,()->new Subsets<Integer>(big));
    }
    
    @Test
    void setTooBigForList(){
        HashSet<Integer> big=new HashSet<>(31);
        for (int i=0;i<31;i++) big.add(i);
        Subsets<Integer> s=new Subsets<>(big);
        assertThrows(IllegalArgumentException.class,()->s.asList());
    }

    @Test
    void beyondBound(){
        Set<TestEnum> input=EnumSet.noneOf(TestEnum.class);
        Iterator<Set<TestEnum>> it=new Subsets<>(input).iterator();
        it.next();
        assertThrows(NoSuchElementException.class,()->it.next());
    }
    
    @Test
    void streaming(){
        Set<Integer> small=new HashSet<>(8);
        for (int i=0;i<8;i++) small.add(i);
        Subsets<Integer> subsets=new Subsets<>(small);
        assertEquals(28,subsets.stream().filter(set->set.size()==2).count());
    }
    
    @Test
    void streamingOrdered(){
        Set<Integer> small=new TreeSet<>();
        for (int i=0;i<8;i++) small.add(i);
        Subsets<Integer> subsets=new Subsets<>(small);
        List<Set<Integer>> expected=Arrays.asList(
            Collections.singleton(0),
            Collections.singleton(1),
            Collections.singleton(2),
            Collections.singleton(3),
            Collections.singleton(4),
            Collections.singleton(5),
            Collections.singleton(6),
            Collections.singleton(7)
        );
        assertEquals(expected,subsets.stream().filter(set->set.size()==1).collect(Collectors.toList()));
    }
}