package com.ledo.sdxl;

import java.util.ArrayList;
import java.util.List;

public class SkipNode<T> {
	
	SkipNode<T> down;
	SkipNode<T> next;
	int score;
	List<T> objs = new ArrayList<T>();
	
	private SkipNode() {}
	
	private SkipNode(int score, T t) {
		this.score = score;
		objs.add(t);
	}
	
	public static <T> SkipNode<T> createNode(int score, T t) {
		return new SkipNode<T>(score, t);
	}
	
	public static <T> SkipNode<T> emptyNode() {
		return new SkipNode<T>();
	}
	
	public void add(T t) {
		objs.add(t);
	}
	
	@Override
	public String toString() {
		if (next == null) {
			return score + "->" + objs;
		} else {
			return score + "->" + objs + ", " + next;
		}
	}
	
}
