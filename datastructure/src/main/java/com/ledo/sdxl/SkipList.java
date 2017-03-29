package com.ledo.sdxl;

import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * 
 * @author caijiahe
 *
 * @param <T> 跳跃表中的元素类型
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class SkipList<T extends Comparable> {

	int maxLevel;  // 最大层数
	int[] power;   // 生成每层的权重
	SkipNode<T>[] roots; // 每层的根节点数组
	Random rand = new Random();
	
	public SkipList(int maxLevel) {
		if (maxLevel < 1) {
			throw new IllegalArgumentException("maxLevel必须大于0");
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
	 * @return 存在相同分数的节点
	 */
	public boolean insert(T t, int score) {
		checkScore(score);
		
		// 先找到每层的前驱和后继
		SkipNode[] prevs = new SkipNode[maxLevel];
		for (int i = maxLevel - 1; i >= 0; i--) {
			SkipNode prev = forward(score, roots[i]);
			
			// 如果有相同分的节点且t没存在，直接将t加入到该节点的objs中
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
		
		// 每层节点 prev.next = cur; cur.next = next;
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
	 * maxLevel=4的时候power是这样的:
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
			throw new IllegalArgumentException("score的值必须大于0!");
		}
	}
	
	/**
	 * 从node开始向前遍历到第一个分数ra小于等于score的元素。
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
