package com.example.chatapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatapp.databinding.FragmentChatGptBinding

class ChatGptFragment : Fragment() {

    private var _binding: FragmentChatGptBinding? = null
    private val binding get() = _binding!!
    private lateinit var diagnosticHypothesesAdapter: ChatAdapter
    private val viewModel: ChatViewModel by viewModels()
    private val list = mutableListOf<Message>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatGptBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapter()
        setupSendMessageClick()
        setupRecentCard()
        binding.imgWarningDisclaimer.setColorFilter(requireContext().getColor(R.color.grayish_blue))
    }

    private fun setupAdapter() {
        diagnosticHypothesesAdapter = ChatAdapter()
        binding.rcvDiagnosisHypotheses.apply {
            setHasFixedSize(true)
            adapter = diagnosticHypothesesAdapter
            val linearLayout = LinearLayoutManager(requireContext())
            linearLayout.stackFromEnd = true
            layoutManager = linearLayout
        }
    }

    private fun setupSendMessageClick() {
        binding.btnSendMessage.setOnClickListener {
            if (binding.edtSendMessage.text.toString().isEmpty()) {
                //do nothing
            } else {
                binding.groupDisclaimer.isVisible = false
                val message = binding.edtSendMessage.text.toString().trim()
                addToChat(message, Message.SENT_BY_ME)
                binding.edtSendMessage.text?.clear()
                viewModel.callChatGptApi(message)
            }
        }
    }

    private fun addToChat(message: String, sentBy: String) {
        runOnUiThread {
            run {
                list.add(Message(message, sentBy))
                diagnosticHypothesesAdapter.updateList(list)
                binding.rcvDiagnosisHypotheses.smoothScrollToPosition(diagnosticHypothesesAdapter.itemCount)
            }
        }
    }

    private fun addResponse(response: String) {
        list.removeAt(list.size - 1)
        addToChat(response, Message.SENT_BY_BOT)
    }

    private fun setupRecentCard() {
        viewModel.chatMessage.observe(viewLifecycleOwner) { resource ->
            binding.apply {
                when (resource.status) {
                    Status.LOADING -> {
                        addToChat(requireContext().getString(R.string.typing), Message.SENT_BY_BOT)
                    }

                    Status.SUCCESS -> {
                        if (!resource.data.isNullOrEmpty()) {
                            addResponse(resource.data)
                        } else {
                            addResponse("Failed")
                        }
                    }

                    Status.ERROR -> {
                        addResponse(resource.data.toString())
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}