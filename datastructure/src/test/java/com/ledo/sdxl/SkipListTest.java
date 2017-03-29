package com.ledo.sdxl;

import org.junit.Test;

import junit.framework.Assert;

public class SkipListTest {
    
    @Test
    public void testInsertAndGet() {
    	SkipList<Integer> list = new SkipList<Integer>(4);
    	list.insert(10086, 1);
    	list.insert(1003, 2);
    	list.insert(10067, 3);
    	list.insert(0, 3);
    	list.insert(10019, 4);
    	list.insert(10444, 5);
    	Assert.assertEquals((int)list.get(3).get(0), 10067);
    	Assert.assertEquals((int)list.get(3).get(1), 0);
    	Assert.assertEquals((int)list.get(5).get(0), 10444);
    	Assert.assertEquals((int)list.get(6).size(), 0);
    }
    
    @Test
    public void testDelete() {
    	SkipList<Integer> list = new SkipList<Integer>(4);
    	list.insert(10086, 1);
    	list.insert(1003, 2);
    	list.insert(10067, 3);
    	list.insert(0, 3);
    	list.insert(10019, 4);
    	list.insert(10444, 5);
    	list.delete(0, 3);
    	Assert.assertEquals((int)list.get(3).size(), 1);
    	Assert.assertEquals((int)list.get(3).get(0), 10067);
    	Assert.assertEquals((int)list.get(5).get(0), 10444);
    	Assert.assertEquals((int)list.get(6).size(), 0);
    }
    
}
