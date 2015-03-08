package circularRing;

import java.util.Collection;
import java.util.Iterator;



public interface Ring<E> extends Collection<E> {

    public E get(int index) throws IndexOutOfBoundsException;

    public Iterator<E> iterator();

    public int size();
}
