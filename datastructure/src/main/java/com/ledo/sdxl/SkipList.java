package com.ledo.sdxl;

import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * 
 * @author caijiahe
 *
 * @param <T> ��Ծ���е�Ԫ������
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class SkipList<T extends Comparable> {

	int maxLevel;  // ������
	int[] power;   // ����ÿ���Ȩ��
	SkipNode<T>[] roots; // ÿ��ĸ��ڵ�����
	Random rand = new Random();
	
	public SkipList(int maxLevel) {
		if (maxLevel < 1) {
			throw new IllegalArgumentException("maxLevel�������0");
		}
		this.maxLevel = maxLevel;
		this.power = new int[maxLevel];
		this.roots = new SkipNode[maxLevel];
		initPower(power);
		initRoot(roots);
	}
	
	/**
	 * @param t
	 * @param score
	 * @return ������ͬ�����Ľڵ�
	 */
	public boolean insert(T t, int score) {
		checkScore(score);
		
		// ���ҵ�ÿ���ǰ���ͺ��
		SkipNode[] prevs = new SkipNode[maxLevel];
		for (int i = maxLevel - 1; i >= 0; i--) {
			SkipNode prev = forward(score, roots[i]);
			
			// �������ͬ�ֵĽڵ���tû���ڣ�ֱ�ӽ�t���뵽�ýڵ��objs��
			if (score == prev.score) {
				if (!prev.objs.contains(t)) {
					while (prev != null) {
						prev.add(t);
						prev = prev.down;
					}
				}
				return true;
			}
			
			prevs[i] = prev;
		}
		
		// ÿ��ڵ� prev.next = cur; cur.next = next;
		SkipNode down = null;
		int level = randomLevel();
		for (int i = 0; i <= level; i++) {
			SkipNode cur = SkipNode.createNode(score, t);
			
			cur.next = prevs[i].next;
			prevs[i].next = cur;
			
			cur.down = down;
			down = cur;
		}
		return false;
	}
	
	public void delete(T t, int score) {
		checkScore(score);
		
		SkipNode node = roots[maxLevel - 1];
		while (node != null && node.score != score) {
			SkipNode cur = forward(score, node);
			if (cur != null && cur.score == score) {
				SkipNode down = cur;
				do {
					down = down.down;
					cur.objs.remove(t);
					if (cur.objs.size() == 0) {
						cur.down = cur.next.down;
						cur.objs.addAll(cur.next.objs);
						cur.score = cur.next.score;
						cur.next = cur.next.next;
					}
				} while (down != null);
				return;
			} else {
				node = node.down;
			}
		}
	}
	
	public List<T> get(int score) {
		checkScore(score);
		
		SkipNode node = roots[maxLevel - 1];
		while (node != null && node.score != score) {
			SkipNode cur = forward(score, node);
			if (cur != null && cur.score == score) {
				return cur.objs;
			} else {
				node = node.down;
			}
		}
		
		return Collections.EMPTY_LIST;
	}
	
	private void initRoot(SkipNode<T>[] roots) {
		SkipNode<T> down = null;
		for (int i = 0; i < maxLevel; i++) {
			roots[i] = SkipNode.emptyNode();
			roots[i].down = down;
			down = roots[i];
		}
	}
	
	/**
	 * maxLevel=4��ʱ��power��������:
	 * [0, 8, 12, 14]
	 * 
	 * @param power
	 */
	private void initPower(int[] power) {
		power[maxLevel - 1] = (1 << maxLevel) - 1;
		for (int i = maxLevel - 2; i > 0; i--) {
			power[i] = power[i + 1] - (1 << (maxLevel - i - 1));
		}
	}
	
	private void checkScore(int score) {
		if (score <= 0) {
			throw new IllegalArgumentException("score��ֵ�������0!");
		}
	}
	
	/**
	 * ��node��ʼ��ǰ��������һ������raС�ڵ���score��Ԫ�ء�
	 * 
	 * @param score
	 * @param node
	 * @return
	 */
	private SkipNode<T> forward(int score, SkipNode<T> node) {
		while (node.next != null && node.score < score) {
			node = node.next;
		}
		return node;
	}

	private int randomLevel() {
		int num = rand.nextInt((1 << maxLevel));
		for (int i = 0; i < power.length - 1; i++) {
			if (num >= power[i] && num < power[i + 1]) {
				return i;
			} else if (num >= power[maxLevel - 1]) {
				return maxLevel - 1;
			}
		}
		return 0;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (SkipNode root : roots) {
			sb.append(root);
			sb.append("\n");
		}
		return sb.toString();
	}
	
}
