package com.itai.spellcheck.common;

/**
 * @author Itai Peleg
 *         Created on 10/12/2014.
 */
public class Pair<T, K> {
    private T left;
    private K right;

    public Pair(T left, K right) {
        this.left = left;
        this.right = right;
    }

    public T getLeft() {
        return left;
    }

    public K getRight() {
        return right;
    }

    @Override
    public String toString() {
        return "Left:" + left + ", Right:" + right;
    }
}
