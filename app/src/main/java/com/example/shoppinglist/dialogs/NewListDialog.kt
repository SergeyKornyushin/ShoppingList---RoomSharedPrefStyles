package com.example.shoppinglist.dialogs

import android.app.AlertDialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import com.example.shoppinglist.R
import com.example.shoppinglist.databinding.NewListDialogBinding

object NewListDialog {
    fun showDialog(context: Context, listener: Listener, name: String) {
        var dialog: AlertDialog? = null
        val builder = AlertDialog.Builder(context)
        val binding = NewListDialogBinding.inflate(LayoutInflater.from(context))
        builder.setView(binding.root)
        binding.apply {
            etNewListName.setText(name)
            if (name.isNotEmpty()) binding.dtnCreateList.text = context.getText(R.string.update_list)
            dtnCreateList.setOnClickListener {
                if (etNewListName.text.isNotEmpty()) {
                    listener.onClick(etNewListName.text.toString())
                }
                dialog?.dismiss()
            }
        }
        dialog = builder.create()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(0x00000000))
        dialog?.show()
    }

    interface Listener {
        fun onClick(name: String)
    }
}