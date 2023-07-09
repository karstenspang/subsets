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
import java.util.TreeSet;
import java.util.function.Supplier;

/**
 * Calculates all subsets of a given {@link Set}.
 *<p>
 * This implementation is limited to sets with no more than 62 elements.
 * Trust me, you don't want to use it on anything bigger, since the complexity is O(n*2**n).
 * <p>
 * The ordering of the subsets is determined by the order the elements are
 * returned by the iterator of the input set. For example, if the set in an
 * {@link SortedSet} with elememnts A, B, C (in that order), the order of the
 * subsets will be {@code [], [A], [B], [A, B], [C], [A, C], [B, C], [A, B, C]}
 * @param <T> The type of the elements in the set.
 */
public class Subsets<T> implements Iterable<Set<T>> {
    private final List<T> values;
    private final Supplier<Set<T>> setSupplier;
    
    /**
     * Build the object with the supplied {@link Supplier}
     * @param set The {@link Set} to calculate the subsets of.
     * @param SetSupplier Supplies the {@link Set} implementation to hold the subsets.
     * @throws IllegalArgumentException if the set has more than 62 elements.
     * @throws NullPointerException is {@code set} of {@code setSupplier} is {@code null}.
     */
    public Subsets(Set<T> set,Supplier<Set<T>> setSupplier)
    {
        if (Objects.requireNonNull(set,"set is null").size()>62) throw new IllegalArgumentException("set is too big");
        this.setSupplier=Objects.requireNonNull(setSupplier,"setSupplier is null");
        values=new ArrayList<>(set);
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
    
    @SuppressWarnings("rawtypes")
    private static <U> Supplier<Set<U>> getSupplier(Set<U> set){
        if (set instanceof EnumSet){
            EnumSet enumSet=(EnumSet)set;
            return new EnumSetSupplier<U>(enumSet);
        }
        else if (set instanceof SortedSet){
            SortedSet<U> sortedSet=(SortedSet<U>)set;
            return new SortedSetSupplier<U>(sortedSet.comparator());
        }
        else{
            return HashSet::new;
        }
    }
    
    @SuppressWarnings({"unchecked","rawtypes"})
    private static class EnumSetSupplier<E> implements Supplier<Set<E>> {
        private final EnumSet emptyEnumSet;
        
        public EnumSetSupplier(EnumSet enumSet){
            emptyEnumSet=enumSet.clone();
            emptyEnumSet.clear();
        }
        
        @Override
        public Set<E> get(){
            return (Set<E>)emptyEnumSet.clone();
        }
    }
    
    private static class SortedSetSupplier<E> implements Supplier<Set<E>> {
        private final Comparator<? super E> comparator;
        
        public SortedSetSupplier(Comparator<? super E> comparator){
            this.comparator=comparator;
        }
        
        @Override
        public Set<E> get(){
            if (comparator==null){
                return new TreeSet<E>();
            }
            else{
                return new TreeSet<E>(comparator);
            }
        }
    }
    
    @Override
    public Iterator<Set<T>> iterator(){return new SubsetIterator();}
    
    private class SubsetIterator implements Iterator<Set<T>> {
        private final long upperBound;
        private long pos;
        public SubsetIterator(){
            upperBound=1L<<values.size();
            pos=0L;
        }
        
        @Override
        public boolean hasNext(){return pos<upperBound;}
        
        @Override
        public Set<T> next(){
            if (!hasNext()) throw new NoSuchElementException();
            Set<T> set=setSupplier.get();
            for (int i=0;i<values.size();i++){
                if (((1L<<i)&pos)!=0){
                    set.add(values.get(i));
                }
            }
            pos++;
            return set;
        }
    }
}
