package com.example.stegochat

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ImageSelectionDialog : BottomSheetDialogFragment() {
    
    private var onGallerySelectedListener: (() -> Unit)? = null
    private var onCameraSelectedListener: (() -> Unit)? = null
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_image_selection, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val galleryOption = view.findViewById<LinearLayout>(R.id.galleryOption)
        val cameraOption = view.findViewById<LinearLayout>(R.id.cameraOption)
        
        galleryOption.setOnClickListener {
            onGallerySelectedListener?.invoke()
            dismiss()
        }
        
        cameraOption.setOnClickListener {
            onCameraSelectedListener?.invoke()
            dismiss()
        }
    }
    
    fun setOnGallerySelectedListener(listener: () -> Unit) {
        onGallerySelectedListener = listener
    }
    
    fun setOnCameraSelectedListener(listener: () -> Unit) {
        onCameraSelectedListener = listener
    }
    
    companion object {
        fun newInstance(
            onGallerySelected: () -> Unit,
            onCameraSelected: () -> Unit
        ): ImageSelectionDialog {
            return ImageSelectionDialog().apply {
                setOnGallerySelectedListener(onGallerySelected)
                setOnCameraSelectedListener(onCameraSelected)
            }
        }
    }
} 