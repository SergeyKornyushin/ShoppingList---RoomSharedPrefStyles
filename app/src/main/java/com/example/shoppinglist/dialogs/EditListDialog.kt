package com.example.shoppinglist.dialogs

import android.app.AlertDialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import com.example.shoppinglist.R
import com.example.shoppinglist.databinding.EditShoppingListDialogBinding
import com.example.shoppinglist.databinding.NewListDialogBinding
import com.example.shoppinglist.entities.ShoppingListItem

object EditListDialog {
    fun showEditDialog(context: Context, shoppingListItem: ShoppingListItem, listener: Listener) {
        var dialog: AlertDialog? = null
        val builder = AlertDialog.Builder(context)
        val binding = EditShoppingListDialogBinding.inflate(LayoutInflater.from(context))
        builder.setView(binding.root)
        binding.apply {
            etEditListName.setText(shoppingListItem.itemName)
            etEditListInfo.setText(shoppingListItem.itemInfo)
            btnEditShoppingList.setOnClickListener {
//                if (!etEditListInfo.text.isNullOrEmpty()) {
                if (etEditListName.text.length > 50) {
                    etEditListName.error = "50 symbols max"
                    return@setOnClickListener
                } else etEditListName.error = null
                if (etEditListInfo.text.length > 100) {
                    etEditListInfo.error = "100 symbols max"
                    return@setOnClickListener
                } else etEditListInfo.error = null
                listener.onClick(
                    shoppingListItem.copy(
                        itemName = etEditListName.text.toString(),
                        itemInfo = etEditListInfo.text.toString()
                    )
                )
                dialog?.dismiss()
//                }
            }
        }
        dialog = builder.create()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(0x00000000))
        dialog?.show()
    }

    interface Listener {
        fun onClick(shoppingListItem: ShoppingListItem)
    }
}