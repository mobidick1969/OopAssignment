package fakecoupangsystem.datastructure.impl;

import fakecoupangsystem.datastructure.Indexing;
import fakecoupangsystem.datastructure.Queue;
import fakecoupangsystem.domain.Order;

import java.nio.BufferUnderflowException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Buzz on 2015. 12. 26..
 * 꽉 차면 두배로, 20% 점유시 사이즈를 절반으로 줄이는 환형 큐
 */
public class CircularPendingQueue implements Queue {

	private Order[] arr;
	private int f;
	private int r;
	private int size;

	public CircularPendingQueue(int len) {
		f = r = size = 0;
		arr = new Order[len];
	}

	@Override
	public void enqueue(Order data) {
		doublingIf();

		if( isEmpty() ) {
			arr[f] = data;
		}
		else {
			f = (f+1)%arr.length;
			arr[f] = data;
		}

		size++;
	}

	@Override
	public Order dequeue() {
		if( isEmpty() ) {
			throw new BufferUnderflowException();
		}

		Order rtn = arr[r];
		r = (r+1)%arr.length;

		size--;
		shrinkIf();
		return rtn;
	}

	private boolean isEmpty() {
		return size <= 0;
	}

	private void setNewArray(int neolen) {
		//shorter than original size * 2 is not allowed
		neolen = Math.max(size*2, neolen);
		//minimum length must 10
		neolen = Math.max(neolen, 10);

		Order neoarr[] = new Order[neolen];

		if( !isEmpty() ) {
			//copy data
			for(int i = r, j = 0 ; ; ) {
				neoarr[j] =
					arr[i];

				if( i == f ) {
					break;
				}

				i = (i+1)%arr.length;
				j++;
			}
		}

		//index reposition
		r = 0;
		f = r + size - 1;
		f = f < 0 ? 0 : f;

		arr = null;
		arr = neoarr;
	}

	private void doublingIf() {
		if( size >= arr.length ) {
			setNewArray(arr.length*2);
		}
	}

	private void shrinkIf() {
		if( size <= arr.length/5 ) {
			setNewArray(arr.length/2);
		}
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public int capacity() {
		return arr.length;
	}

	@Override
	public Order at(int idx) {
		if( idx < 0 || idx >= arr.length ) {
			return null;
		}

		return arr[idx];
	}

	@Override
	public List<Order> find(Indexing indexing, Object target) {
		List<Order> orders = new ArrayList<Order>(5);

		for(int i = 0 ; i<size() ; i++ ) {
			boolean found = false;
			Order order = arr[i];
			switch(indexing) {
				case COUPANG_ID :
					if( order.getCoupangId().equals((String)target) ) {
						found = true;
					}
					break;
				case ORDER_ID:
					if( order.getOrderId() == (Long)target )  {
						found = true;
					}
					break;
				default :
					break;
			}

			if( found ) {
				orders.add(order);
			}
		}

		return orders;
	}


}
