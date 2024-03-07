package com.sciflare.smsapp.fragments

import android.annotation.SuppressLint
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.sciflare.smsapp.MainActivity
import com.sciflare.smsapp.databinding.FragmentSendSmsBinding
import com.sciflare.smsapp.viewmodel.MessagesViewModel
import com.sciflare.smsapp.viewmodel.UIViewState
import com.sciflare.smsapp.viewmodel.ViewModelFactory
import java.util.regex.Pattern


class SendSMSFragment : Fragment() {

    private var _binding: FragmentSendSmsBinding? = null

    private val binding get() = _binding!!
    private lateinit var viewModel: MessagesViewModel
    var callback: OnBackPressedCallback?=null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentSendSmsBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
          callback = object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    isEnabled = false
                    requireActivity().onBackPressed()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(this,
            callback as OnBackPressedCallback
        )
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupObserver()

        binding.imgContacts.setOnClickListener{
            pickContact.launch(null)
        }
        binding.btnSend.setOnClickListener{
            if (validate()){
                val phoneNumber = binding.edtPhoneNumber.text.toString().trim()
                val messageBody = binding.edtMessageArea.text.toString().trim()
                viewModel.sendSMS(requireContext(),phoneNumber,messageBody)
            }
        }

    }

    private fun validate(): Boolean {
        var status = true
        if (binding.edtPhoneNumber.text.isNotEmpty()) {
            val phoneNumber = binding.edtPhoneNumber.text.toString().trim()
            if(!Pattern.matches("[a-zA-Z]+", phoneNumber)) {
                if (phoneNumber.length in 7..13){

                }else{
                    status =  false
                    binding.edtPhoneNumber.error = "Invalid mobile number"
                }
            }else{
                status = false
                binding.edtPhoneNumber.error = "Invalid mobile number"
            }
        } else {
            status = false
            binding.edtPhoneNumber.error = "Please enter mobile number"
        }

        if (binding.edtMessageArea.text.isEmpty()) {
            status = false
            binding.edtMessageArea.error = "Please enter the message"
        }
        return status
    }

    private fun setupViewModel(){
        viewModel = ViewModelProvider(this, ViewModelFactory())[MessagesViewModel::class.java]
    }

    private fun setupObserver(){
        viewModel.getSendSMSUIState().observe(viewLifecycleOwner) {
            when (it) {
                is UIViewState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(context, "SMS sent, Swipe to refresh!", Toast.LENGTH_SHORT).show()
                    requireActivity().onBackPressed()
                }

                is UIViewState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }

                is UIViewState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(context, "Error in sending SMS", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(context, "Error in sending SMS", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
     * Activity for result for picking contact from Android contacts
     */
    @SuppressLint("Range")
    private val pickContact = registerForActivityResult<Void, Uri>(ActivityResultContracts.PickContact()
    ) { result ->
        if (result != null) {

            val cursor1: Cursor?
            val cursor2: Cursor?

            val uri: Uri = result

            cursor1 = requireContext().contentResolver.query(uri, null, null, null, null)

            if (cursor1!!.moveToFirst()) {
                //get contact details
                val contactId =
                    cursor1.getString(cursor1.getColumnIndex(ContactsContract.Contacts._ID))
                //                    val contactName = cursor1.getString(cursor1.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                val idResults =
                    cursor1.getString(cursor1.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))
                val idResultHold = idResults.toInt()
                if (idResultHold == 1) {
                    cursor2 = requireContext().contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId,
                        null,
                        null
                    )
                    //a contact may have multiple phone numbers
                    while (cursor2!!.moveToNext()) {
                        //get phone number
                        val contactNumber =
                            cursor2.getString(cursor2.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                        if (contactNumber.isNotEmpty()) {
                            val number = contactNumber.replace(" ", "")
                            binding.edtPhoneNumber.setText(number)
                        }
                        Log.d("MAIN_ACTIVITY", "number: $contactNumber")
                    }
                    cursor2.close()
                }
                cursor1.close()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}