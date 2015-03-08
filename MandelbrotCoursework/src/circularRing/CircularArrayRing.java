package circularRing;

import java.util.AbstractCollection;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @author Will
 *
 * @param <E>
 */
public class CircularArrayRing<E> extends AbstractCollection<E> implements Ring<E>
{
	private E[] queue;
	private int head = 0; // Head is the front of the queue, tail is at the
	private int tail = 0; // back
	private int noItems;
	private final int DEFAULT_CAPACITY = 5;

	@SuppressWarnings("unchecked")
	public CircularArrayRing() // Starts both the head and tail index at the
								// first element of the array
	{
		setQueue((E[]) new Object[DEFAULT_CAPACITY]);
		setHead(0);
		setTail(0);
	}

	@SuppressWarnings("unchecked")
	public CircularArrayRing(int size) // Starts both the head and tail index at
										// the
	// first element of the array
	{
		setQueue((E[]) new Object[size]);
		setHead(0);
		setTail(0);
	}

	@Override
	public boolean add(E e) // Inserts the desired element at the tail of
							// the queue, then increments the tail index
	{
		if (getQueue()[getTail()] == null)
			setNoItems(getNoItems() + 1);
		getQueue()[getTail()] = e;
		setTail((getTail() + 1) % getQueue().length);
		return true;
	}

	
	/**
	 * Fetches the element contained at the specified index of the ring
	 */
	@Override
	public E get(int index) throws IndexOutOfBoundsException
	{
		if (index >= size() || index > getQueue().length || index < 0)
			throw new IndexOutOfBoundsException();
		if (index + 1 <= getTail())
			return getQueue()[getTail() - (index + 1)];
		else
			return getQueue()[getQueue().length + (getTail() - (index + 1))];
	}

	@Override
	public int size()
	{
		return getNoItems();
	}

	@Override
	public boolean isEmpty()
	{
		return getNoItems() == 0;
	}

	public boolean isFull()
	{
		return getNoItems() == getQueue().length;
	}

	public int getCapacityLeft()
	{
		return getQueue().length - size();
		/*
		 * if (!isEmpty()) return queue.length - (tail - head); else return
		 * queue.length;
		 */
	}

	@Override
	public Iterator<E> iterator()
	{
		Iterator<E> it = new Iterator<E>()
		{

			private int currentIndex = 0;

			@Override
			public boolean hasNext()
			{
				return (currentIndex < size());
			}

			@Override
			public E next()
			{
				if (currentIndex < size())
				{
					E nextItem = get(currentIndex);
					++currentIndex;
					return nextItem;
				}
				else
					throw new NoSuchElementException();
			}

			@Override
			public void remove()
			{
				throw new UnsupportedOperationException();
			}
		};
		return it;
	}

	private int getHead()
	{
		return head;
	}

	private void setHead(int head)
	{
		this.head = head;
	}

	private int getTail()
	{
		return tail;
	}

	private void setTail(int tail)
	{
		this.tail = tail;
	}

	private E[] getQueue()
	{
		return queue;
	}

	private void setQueue(E[] queue)
	{
		this.queue = queue;
	}

	private int getNoItems()
	{
		return noItems;
	}

	private void setNoItems(int noItems)
	{
		this.noItems = noItems;
	}

}
