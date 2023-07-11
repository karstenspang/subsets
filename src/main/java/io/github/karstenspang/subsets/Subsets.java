package io.github.karstenspang.subsets;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.Spliterator;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Calculates all subsets of a given {@link Set}.
 *<p>
 * This implementation is limited to sets with no more than 62 elements.
 * Trust me, you don't want to use it on anything bigger, since the complexity is O(n*2**n).
 * <p>
 * The ordering of the subsets is determined by the order the elements are
 * returned by the iterator of the input set. For example, if the set is a
 * {@link SortedSet} with elements {@code [A, B, C]} (in that order), the order of the
 * subsets will be {@code [], [A], [B], [A, B], [C], [A, C], [B, C], [A, B, C]}
 * @param <T> The type of the elements in the set.
 */
public class Subsets<T> implements Iterable<Set<T>> {
    private final Object[] elements;
    private final Supplier<Set<T>> setSupplier;
    private final int ordered;
    private final int size;
    
    /**
     * Build the object with the supplied {@link Supplier}
     * @param set The {@link Set} to calculate the subsets of.
     * @param setSupplier Supplies the {@link Set} implementation to hold the subsets.
     * @throws IllegalArgumentException if the set has more than 62 elements.
     * @throws NullPointerException is {@code set} of {@code setSupplier} is {@code null}.
     */
    public Subsets(Set<T> set,Supplier<Set<T>> setSupplier)
    {
        if (Objects.requireNonNull(set,"set is null").size()>62) throw new IllegalArgumentException("set is too big");
        this.setSupplier=Objects.requireNonNull(setSupplier,"setSupplier is null");
        elements=new ArrayList<>(set).toArray();
        size=elements.length;
        ordered=(set instanceof EnumSet || set instanceof SortedSet)?Spliterator.ORDERED:0;
        
    }
    
    /**
     * Build the object selecting a {@link Supplier} based on the runtime
     * type of the input set.
     * <ul>
     *  <li>If the input is an {@link EnumSet}, the output will be {@link EnumSet}s as well.</li>
     *  <li>If the input is a {@link SortedSet}, the output will be {@link SortedSet}s with the same ordering.</li>
     *  <li>Otherwise the ordering of the output is unspecified.</li>
     * </ul>
     * @param set The {@link Set} to calculate the subsets of.
     * @throws IllegalArgumentException if the set has more than 62 elements.
     * @throws NullPointerException is {@code set} is {@code null}.
     */
    public Subsets(Set<T> set){
        this(set,getSupplier(set));
    }
    
    @SuppressWarnings({"unchecked","rawtypes"})
    private static <U> Supplier<Set<U>> getSupplier(Set<U> set){
        if (set instanceof EnumSet){
            // Raw types are needed here, because otherwise the
            // compiler will complain about type parameter bounds.
            // It cannot tell that U is a enum type. Since the set is
            // an EnumSet, it can be assumed that this is the case.
            @SuppressWarnings("rawtypes")
            EnumSet enumSet=(EnumSet)set;
            @SuppressWarnings("rawtypes")
            final EnumSet emptyEnumSet=enumSet.clone();
            emptyEnumSet.clear();
            @SuppressWarnings("unchecked")
            Supplier<Set<U>> supplier=(Supplier<Set<U>>)emptyEnumSet::clone;
            return supplier;
        }
        else if (set instanceof SortedSet){
            SortedSet<U> sortedSet=(SortedSet<U>)set;
            final Comparator<? super U> comparator=sortedSet.comparator();
            if (comparator==null){
                return TreeSet::new;
            }
            else{
                return ()->new TreeSet<>(comparator);
            }
        }
        else{
            return HashSet::new;
        }
    }
    
    /**
     * Get an {@link Iterator} over the subsets.
     */
    @Override
    public Iterator<Set<T>> iterator(){return new SubsetIterator();}
    
    private class SubsetIterator implements Iterator<Set<T>> {
        private final long upperBound;
        private long pos;
        
        public SubsetIterator(){
            upperBound=1L<<size;
            pos=0L;
        }
        
        @Override
        public final boolean hasNext(){return pos<upperBound;}
        
        @Override
        public Set<T> next(){
            if (!hasNext()) throw new NoSuchElementException();
            Set<T> set=getSubset(pos);
            pos++;
            return set;
        }
    }
    
    private final Set<T> getSubset(long pos){
        Set<T> set=setSupplier.get();
        for (int i=0;i<size;i++){
            if (((1L<<i)&pos)!=0){
                @SuppressWarnings("unchecked")
                T element=(T)elements[i];
                set.add(element);
            }
        }
        return set;
    }
    /**
     * Materialize the list of subsets as a {@link List}.
     * Only recommended for small sets.
     * @return the list of subsets.
     * @throws IllegalArgumentException if the set has more than 30 elements.
     */
    public List<Set<T>> asList(){
        if (elements.length>30) throw new IllegalArgumentException();
        List<Set<T>> result=new ArrayList<>(1<<elements.length);
        for (Set<T> subset:this){
            result.add(subset);
        }
        return result;
    }
    
    /**
     * Get a {@link Spliterator} over the subsets.
     */
    @Override
    public Spliterator<Set<T>> spliterator(){
        return new SubsetSpliterator(0L,1L<<size);
    }
    
    private class SubsetSpliterator implements Spliterator<Set<T>> {
        private final long high;
        private long pos;
        
        public SubsetSpliterator(long low,long high){
            this.high=high;
            this.pos=low;
        }
        
        @Override
        public SubsetSpliterator trySplit(){
            if (high-pos<2L) return null;
            long mid=(pos+high)/2L;
            SubsetSpliterator head=new SubsetSpliterator(pos,mid);
            pos=mid;
            return head;
        }
        
        @Override
        public boolean tryAdvance(Consumer<? super Set<T>> action){
            if (pos==high) return false;
            Set<T> set=getSubset(pos);
            action.accept(set);
            pos++;
            return true;
        }
        
        @Override
        public int characteristics(){
            return Spliterator.DISTINCT|Spliterator.IMMUTABLE|Spliterator.NONNULL|ordered|Spliterator.SIZED|Spliterator.SUBSIZED;
        }
        
        @Override
        public long estimateSize(){return high-pos;}
    }
    
    /**
     * Get a {@link Stream} of the subsets.
     */
    public Stream<Set<T>> stream(){
        return StreamSupport.stream(spliterator(),true);
    }
}
