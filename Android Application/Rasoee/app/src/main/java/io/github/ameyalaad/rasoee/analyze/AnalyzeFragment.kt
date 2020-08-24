package io.github.ameyalaad.rasoee.analyze

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import io.github.ameyalaad.rasoee.R
import io.github.ameyalaad.rasoee.databinding.FragmentAnalyzeBinding
import java.io.File
import java.io.IOException


class AnalyzeFragment : Fragment() {
    private val viewModel: AnalyzeViewModel by lazy {
        ViewModelProvider(this).get(AnalyzeViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentAnalyzeBinding.inflate(inflater)

        binding.setLifecycleOwner(this)
        binding.viewModel = viewModel

//        binding.cameraCaptureButton.setOnClickListener {
//            dispatchTakePictureIntent()
//        }
//
//        binding.addFromGalleryButton.setOnClickListener {
//            pickFromGallery()
//        }
        registerForContextMenu(binding.addImageButton)
        binding.addImageButton.setOnClickListener {
            it.showContextMenu()
            return@setOnClickListener
        }
        registerForContextMenu(binding.displayImageButton)
        binding.displayImageButton.setOnClickListener {
            it.showContextMenu()
            return@setOnClickListener
        }

        setHasOptionsMenu(true)

        binding.analyzeButton.setOnClickListener {
            if (viewModel.photoBitmap.value == null) {
                Toast.makeText(context, "Please load an image first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.analyzeImage()
            it.visibility = View.GONE
        }

        binding.recipeList.adapter =
            RecipeListAdapter(
                RecipeListAdapter.OnClickListener {
                    viewModel.displayPropertyDetails(it)
                })


        viewModel.navigateToSelectedProperty.observe(viewLifecycleOwner, Observer {
            if (null != it) {
                this.findNavController().navigate(
                    AnalyzeFragmentDirections.Companion.actionAnalyzeFragmentToDetailFragment(
                        it
                    )
                )
                viewModel.displayPropertyDetailsComplete()
            }
        })
        return binding.root
    }

    // Context menu for popup
    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menu.setHeaderTitle("Choose an image:")
        activity?.menuInflater?.inflate(R.menu.add_image_menu, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_capture_button -> {
                dispatchTakePictureIntent()
            }
            R.id.menu_gallery_button -> {
                pickFromGallery()
            }
        }
        return true
    }

    // Options menu for reset
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.options_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.options_reset_button -> {
                viewModel.reset()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_TAKE_PHOTO -> viewModel.setPhoto()
                GET_FROM_GALLERY -> viewModel.setPhotoFromGallery(data?.data!!)
            }
        }
    }

    val REQUEST_TAKE_PHOTO = 1
    val GET_FROM_GALLERY = 2

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    viewModel.createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    Toast.makeText(
                        context,
                        "An error occured while creating the file",
                        Toast.LENGTH_LONG
                    ).show()
                    null // return null as the file object
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        requireContext(),
                        "com.example.android.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
                }
            }
        }
    }

    private fun pickFromGallery() {
        Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.INTERNAL_CONTENT_URI
        ).also { getImageIntent ->
            // Ensure that there's a gallery activity to handle the intent
            getImageIntent.resolveActivity(requireActivity().packageManager)?.also {
                startActivityForResult(getImageIntent, GET_FROM_GALLERY)
            }
        }
    }
}