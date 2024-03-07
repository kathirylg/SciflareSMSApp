package com.sciflare.smsapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.sciflare.smsapp.R
import com.sciflare.smsapp.adapters.MessageListAdapter
import com.sciflare.smsapp.databinding.FragmentSmsBinding
import com.sciflare.smsapp.model.MessageModel
import com.sciflare.smsapp.viewmodel.MessagesViewModel
import com.sciflare.smsapp.viewmodel.UIViewState
import com.sciflare.smsapp.viewmodel.ViewModelFactory


class SMSFragment : Fragment() {

    private var _binding: FragmentSmsBinding? = null

    private val binding get() = _binding!!

    private var smsList = ArrayList<MessageModel>()
    private lateinit var adapter:MessageListAdapter

    private lateinit var viewModel: MessagesViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentSmsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSendSms.setOnClickListener {
            findNavController().navigate(R.id.action_MessagesFragment_to_CreateSMSFragment)
        }

        setupViewModel()
        setUpUI()
        viewModel.launchMessages.observe(viewLifecycleOwner) {
            setupObserver()
            viewModel.getAllMessages(requireContext())
        }

        binding.swipeContainer.setOnRefreshListener {
            viewModel.getAllMessages(requireContext())
        }
    }

    private fun updateAdapter(){
        binding.rcyMessages.visibility = View.VISIBLE
        adapter.addData(smsList)
    }

    private fun setUpUI() {
        binding.rcyMessages.layoutManager = LinearLayoutManager(context)
        adapter = MessageListAdapter { _ ->

        }
        binding.rcyMessages.addItemDecoration(
            DividerItemDecoration(
                context,
                (binding.rcyMessages.layoutManager as LinearLayoutManager).orientation
            )
        )
        binding.rcyMessages.adapter = adapter
    }

   private fun setupViewModel(){
        viewModel = activity.run {
            ViewModelProvider(requireActivity(), ViewModelFactory())[MessagesViewModel::class.java]
        }
    }

    private fun setupObserver(){
        viewModel.getUIState().observe(viewLifecycleOwner) {
            when (it) {
                is UIViewState.Success -> {
                    smsList = it.data
                    binding.progressBar.visibility = View.GONE
                    binding.swipeContainer.isRefreshing = false
                    updateAdapter()
                }

                is UIViewState.Update->{
                    smsList.addAll(0, it.data)
                    binding.progressBar.visibility = View.GONE
                    updateAdapter()
                }

                is UIViewState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.rcyMessages.visibility = View.GONE
                }

                is UIViewState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(context, "Error in fetching SMS", Toast.LENGTH_SHORT).show()
                }

                else -> {
                    Toast.makeText(context, "Error in fetching SMS", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}