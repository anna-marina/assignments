package hw4

import java.io.*

/* hw4 trees - functional-like style */

abstract class Tree<T: Comparable<T>>() : Set<T> {
    var h: Int = 0
    var d: Int = 0

    fun rotLeft(): Tree<T> =
        if (this is Node && r is Node)
            Node(r.v, Node(v, l, r.l), r.r)
        else
            Nil()

    fun rotRight(): Tree<T> =
        if (this is Node && l is Node)
            Node(l.v, l.l, Node(v, l.r, r))
        else
            Nil()

    fun rotLeftRight(): Tree<T> =
        if (this is Node)
            Node(v, l.rotLeft(), r).rotRight()
        else
            Nil()

    fun rotRightLeft(): Tree<T> =
        if (this is Node)
            Node(v, l, r.rotRight()).rotLeft()
        else
            Nil()

    override fun insert(x: T): Tree<T> =
        if (this is Node) (
            if (x < v)
                Node(v, l.insert(x), r)
            else
                Node(v, l, r.insert(x))
            ).fix()
        else
            Node(x, Nil(), Nil())

    fun removeRightmost(): Pair<T, Tree<T>>? =
        if (this is Node)
            if (r is Node) {
                val t = r.removeRightmost()!!
                Pair(t.first, Node(v, l, t.second))
            } else
                Pair(v, l)
        else
            null /* Never reached, !! always OK */

    override fun remove(x: T): Tree<T> =
        if (this is Node) (
            if (x < v)
                Node(v, l.remove(x), r)
            else if (x > v)
                Node(v, l, r.remove(x))
            else
                when {
                    l is Node && r is Node -> {
                        val t = l.removeRightmost()!!
                        Node(t.first, t.second, r)
                    }
                    l is Node -> r
                    r is Node -> l
                    else -> this
                }).fix()
        else
            Nil()


    fun fix(): Tree<T> =
        if (this is Node)
            when {
                d == -2 ->
                    if (r.d <= 0) this.rotLeft()
                    else this.rotRightLeft()
                d == 2 ->
                    if (l.d >= 0) this.rotRight()
                    else this.rotLeftRight()
                else ->
                    this
            }
        else
            Nil()

    override fun find(x: T): Boolean =
        if (this is Node)
            when {
                x < v -> l.find(x)
                x > v -> r.find(x)
                else -> true
            }
        else
            false

    fun print(w: Writer) {
        if (!(this is Node))
            w.write("x")
        else {
            if (r is Node) {
                r.printRec(true, w, "");
            }
            w.write(v.toString() + "\n");
            if (l is Node) {
                l.printRec(false, w, "");
            }
        }
    }

    fun printRec(isr: Boolean, w: Writer, pre: String) {
        if (!(this is Node))
            return
        if (r is Node)
            r.printRec(true, w, pre + (if (isr) "     " else " |   "))
        w.write(pre + (if (isr) " /" else " \\") + "--- " + v.toString() + "\n")
        if (l is Node)
            l.printRec(false, w, pre + (if (isr) " |   " else "     "))
    }

    fun text(): String =
        if (this is Node)
            "[" + v.toString() + "," + l.text() + "," + r.text() + "]"
        else
            "[]"
}

class Nil<T: Comparable<T>>(): Tree<T>()

class Node<T: Comparable<T>>(val v: T, val l: Tree<T>, val r: Tree<T>): Tree<T>() {
    init {
        h = Math.max(l.h, r.h) + 1
        d = l.h - r.h
    }
}

fun main(args: Array<String>) {
    var t: Tree<Int> = Nil()
    val w = OutputStreamWriter(System.out)
    val a = arrayOf(5, 4, 6, 2, 1, 3, 7)
    for (x in a) {
        t = t.insert(x)
        t.print(w)
    }
    t = t.remove(4)
    t.print(w)
    t = t.remove(3)
    t.print(w)
    t = t.remove(2)
    t.print(w)
    w.close()
}