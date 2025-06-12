package com.example.trimly.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.trimly.R
import com.google.android.libraries.places.api.model.PhotoMetadata
import com.google.android.libraries.places.api.net.FetchPhotoRequest
import com.google.android.libraries.places.api.net.PlacesClient

class SalonPhotosAdapter(
    private val placesClient: PlacesClient,
    private val photoMetadatas: List<PhotoMetadata>
) : RecyclerView.Adapter<SalonPhotosAdapter.PhotoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_salon_photo, parent, false)
        return PhotoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val photoMetadata = photoMetadatas[position]
        holder.bind(photoMetadata)
    }

    override fun getItemCount() = photoMetadatas.size

    inner class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.ivSalonPhoto)

        fun bind(photoMetadata: PhotoMetadata) {
            // Очищення та placeholder
            imageView.setImageDrawable(null)
            imageView.setBackgroundColor(itemView.context.getColor(android.R.color.darker_gray))

            val photoRequest = FetchPhotoRequest.builder(photoMetadata)
                .setMaxWidth(1200)
                .setMaxHeight(800)
                .build()

            placesClient.fetchPhoto(photoRequest)
                .addOnSuccessListener { fetchPhotoResponse ->
                    imageView.setImageBitmap(fetchPhotoResponse.bitmap)
                    imageView.animate().alpha(1f).setDuration(200).start() // fade-in
                    imageView.setBackgroundColor(0x00000000) // прибираємо фон
                }
                .addOnFailureListener {
                    imageView.setImageDrawable(null)
                    imageView.setBackgroundColor(itemView.context.getColor(android.R.color.darker_gray))
                }
        }
    }
} 