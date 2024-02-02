package com.gchristov.thecodinglove.common.kotlin

fun parseMainArgs(args: Array<String>): Map<String, List<String>> =
    args.fold(mutableListOf()) { acc: MutableList<MutableList<String>>, s: String ->
        acc.apply {
            if (s.startsWith('-')) add(mutableListOf(s))
            else last().add(s)
        }
    }.associate { it[0] to it.drop(1) }
