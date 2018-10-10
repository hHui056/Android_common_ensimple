package com.allen.androidcommonexample.test

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.allen.androidcommonexample.R
import com.allen.androidcommonexample.rxbus.TestBean
import java.util.*

/**
 * Created by hHui on 2017/10/27.
 */

class SpinnerListAdapter(internal var context: Context, internal var list: ArrayList<TestBean>) : BaseAdapter() {

    var viewHolder: ViewHolder? = null

    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(position: Int): Any? {
        return null
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        if (convertView == null) {
            viewHolder = ViewHolder()
            val inflater = LayoutInflater.from(context)
            convertView = inflater.inflate(R.layout.spinner_layout, parent, false)
            viewHolder!!.text = convertView!!.findViewById<TextView>(R.id.txt_name)
            viewHolder!!.spinner = convertView.findViewById<Spinner>(R.id.my_sp)

            convertView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ViewHolder
        }

        val arrAdapter = ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, list[position].data)
        arrAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        viewHolder!!.spinner!!.adapter = arrAdapter
        viewHolder!!.spinner!!.setSelection(0)
        viewHolder!!.text!!.text = list[position].name

        viewHolder!!.spinner!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, mposition: Int, id: Long) {
                System.out.println("==================$position   $mposition")
            }

        }
        return convertView
    }

    inner class ViewHolder {
        internal var text: TextView? = null
        internal var spinner: Spinner? = null
    }
}
