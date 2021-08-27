package com.zainco.codingflowtodo.util

import androidx.appcompat.widget.SearchView


inline fun SearchView.onQueryTextChanged(crossinline listener: (String) -> Unit) {
    this.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {//we don't care about this method, so we created this extension function
            //(don't want this function in our fragment code)
            return true
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            listener(newText.orEmpty()/*returns empty string if this newText is null*/)/*return might be called inside listener*/
            return true/*in order to reach this return crossinline should be added,*/
        }

    })
}

