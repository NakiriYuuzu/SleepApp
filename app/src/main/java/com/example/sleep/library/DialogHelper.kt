package com.example.sleep.library

import android.app.Activity
import android.content.DialogInterface
import com.example.sleep.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class DialogHelper(
    private val context: Activity,
) {
    private var loading = false
    private val loadingView = context.layoutInflater.inflate(R.layout.dialog_loading, null, false)
    private var loadingDialog = MaterialAlertDialogBuilder(context, R.style.Style_CustomDialog).create()

    init {
        loadingDialog.setView(loadingView)
        loadingDialog.setCancelable(false)
        loadingDialog.setCanceledOnTouchOutside(false)
        loadingDialog.setTitle("Loading")
    }

    fun showDialog(title: String, message: String, cancelable: Boolean) {
        MaterialAlertDialogBuilder(context, R.style.Style_CustomDialog)
            .setTitle(title)
            .setMessage(message)
            .setCancelable(cancelable)
            .setPositiveButton("確定", null)
            .show()
    }

    fun showDialog(title: String, message: String, cancelable: Boolean, positiveButton: String, positiveButtonListener: OnPositiveListener) {
        MaterialAlertDialogBuilder(context, R.style.Style_CustomDialog)
            .setTitle(title)
            .setMessage(message)
            .setCancelable(cancelable)
            .setPositiveButton(positiveButton) { dialog, i ->
                positiveButtonListener.onPositiveClick(dialog, i)
            }
            .show()
    }

    fun showDialog(title: String, message: String, cancelable: Boolean, positiveButton: String, negativeButton: String, onDialogListener: OnDialogListener) {
        MaterialAlertDialogBuilder(context, R.style.Style_CustomDialog)
            .setTitle(title)
            .setMessage(message)
            .setCancelable(cancelable)
            .setPositiveButton(positiveButton) { dialog, i ->
                onDialogListener.onPositiveClick(dialog, i)
            }
            .setNegativeButton(negativeButton) { dialog, i ->
                onDialogListener.onNegativeClick(dialog, i)
            }
            .show()
    }

    private fun isLoading() : Boolean {
        return loading
    }

    fun showLoading() {
        if (!isLoading()) {
            loading = true
            loadingDialog.show()
        }
    }

    fun hideLoading() {
        if (isLoading()) {
            loading = false
            loadingDialog.dismiss()
        }
    }

    interface OnPositiveListener {
        fun onPositiveClick(dialogInterface: DialogInterface?, i: Int)
    }

    interface OnDialogListener {
        fun onPositiveClick(dialog: DialogInterface?, which: Int)
        fun onNegativeClick(dialog: DialogInterface?, which: Int)
    }
}
