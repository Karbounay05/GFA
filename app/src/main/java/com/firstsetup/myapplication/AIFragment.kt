package com.firstsetup.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class AIFragment : Fragment() {

    private lateinit var card: CardView
    private lateinit var aiBubble: ImageView
    private lateinit var overlayBackground: View
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ChatAdapter
    private val messages = mutableListOf<Message>()
    private val aiAssistant = AiAssistant()

    private var isSleeping = false
    var isExpanded = false // Initially minimized

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_ai, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        card = view.findViewById(R.id.ai_card)
        aiBubble = view.findViewById(R.id.ai_bubble)
        overlayBackground = view.findViewById(R.id.overlay_background)
        recyclerView = view.findViewById(R.id.recyclerViewChat)

        val closeButton = view.findViewById<ImageButton>(R.id.closeButton)
        val minimizeButton = view.findViewById<ImageButton>(R.id.minimizeButton)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = ChatAdapter(messages) { userChoice ->
            simulateTyping(userChoice)
        }
        recyclerView.adapter = adapter

        aiAssistant.loadConversation(requireContext()) // <<< very important

        aiAssistant.startConversation(object : AiAssistant.Callback {
            override fun onAiRespond(message: String, options: List<String>) {
                addAiMessage(message)
                recyclerView.postDelayed({
                    addOptions(options)
                }, 1000) // wait 1s before showing options
            }
        })


        closeButton.setOnClickListener {
            (activity as? Acceuil)?.showAccueilButtons()
            requireActivity().supportFragmentManager.popBackStack()
        }

        minimizeButton.setOnClickListener {
            minimizePopup()
            (activity as? Acceuil)?.showAccueilButtons()
        }

        aiBubble.setOnClickListener {
            if (!isExpanded) {
                expandPopup()
            }
        }

        overlayBackground.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN && isExpanded) {
                val cardLocation = IntArray(2)
                card.getLocationOnScreen(cardLocation)

                val cardLeft = cardLocation[0]
                val cardTop = cardLocation[1]
                val cardRight = cardLeft + card.width
                val cardBottom = cardTop + card.height

                val clickX = event.rawX.toInt()
                val clickY = event.rawY.toInt()

                if (clickX < cardLeft || clickX > cardRight || clickY < cardTop || clickY > cardBottom) {
                    (activity as? Acceuil)?.showAccueilButtons()
                    requireActivity().supportFragmentManager.popBackStack()
                }
            }
            true
        }

        enableDrag(aiBubble)
    }

    private fun simulateTyping(userChoice: String) {
        // 1. Add user message first
        messages.add(Message(userChoice, false, isUser = true))
        adapter.notifyItemInserted(messages.size - 1)
        recyclerView.scrollToPosition(messages.size - 1)

        // 2. After 1 second, show "Assistant écrit..."
        recyclerView.postDelayed({
            val typingMessage = Message("Assistant écrit...", false)
            messages.add(typingMessage)
            adapter.notifyItemInserted(messages.size - 1)
            recyclerView.scrollToPosition(messages.size - 1)

            // 3. After another 1 second, remove "Assistant écrit..." and show real AI response
            recyclerView.postDelayed({
                val typingIndex = messages.indexOf(typingMessage)
                if (typingIndex != -1) {
                    messages.removeAt(typingIndex)
                    adapter.notifyItemRemoved(typingIndex)
                }

                // Call AI to get response
                aiAssistant.handleUserChoice(userChoice, object : AiAssistant.Callback {
                    override fun onAiRespond(message: String, options: List<String>) {
                        addAiMessage(message)
                        recyclerView.postDelayed({
                            addOptions(options)
                        }, 1000)
                    }
                })

            }, 1300) // another 1 second after "Assistant écrit..."
        }, 500) // first 1 second after user message
    }


    private fun addAiMessage(text: String) {
        messages.add(Message(text, false))
        adapter.notifyItemInserted(messages.size - 1)
        recyclerView.scrollToPosition(messages.size - 1)
    }

    private fun addOptions(options: List<String>) {
        options.forEach { option ->
            messages.add(Message(option, true))
        }
        adapter.notifyItemRangeInserted(messages.size - options.size, options.size)
        recyclerView.scrollToPosition(messages.size - 1)
    }

    private fun addUserMessage(choice: String) {
        messages.add(Message(choice, false, isUser = true))
        adapter.notifyItemInserted(messages.size - 1)
        recyclerView.scrollToPosition(messages.size - 1)
    }

    fun expandPopup() {
        if (isSleeping) {
            aiBubble.animate()
                .alpha(1f)
                .setDuration(200)
                .start()
            isSleeping = false
        }

        (activity as? Acceuil)?.hideAccueilButtons() // 🔥 Hide buttons when expanding

        overlayBackground.visibility = View.VISIBLE
        overlayBackground.isClickable = true
        overlayBackground.isFocusable = true

        card.bringToFront()

        aiBubble.animate()
            .scaleX(1.2f).scaleY(1.2f).alpha(0f).setDuration(200)
            .withEndAction {
                aiBubble.visibility = View.GONE
                card.scaleX = 0.8f
                card.scaleY = 0.8f
                card.alpha = 0f
                card.visibility = View.VISIBLE
                card.animate()
                    .scaleX(1f).scaleY(1f).alpha(1f)
                    .setDuration(300)
                    .start()
                isExpanded = true
            }.start()
    }

    private fun minimizePopup() {
        card.animate()
            .scaleX(0.8f).scaleY(0.8f).alpha(0f).setDuration(200)
            .withEndAction {
                card.visibility = View.GONE

                overlayBackground.visibility = View.GONE
                overlayBackground.isClickable = false
                overlayBackground.isFocusable = false

                aiBubble.scaleX = 1.2f
                aiBubble.scaleY = 1.2f
                aiBubble.alpha = 0f
                aiBubble.visibility = View.VISIBLE

                aiBubble.animate()
                    .scaleX(1f).scaleY(1f).alpha(1f) // 🔥 appear with FULL opacity first
                    .setDuration(300)
                    .withEndAction {
                        // 🔥 After appearing, wait 5 sec then fade to 0.5
                        aiBubble.postDelayed({
                            aiBubble.animate()
                                .alpha(0.5f)
                                .setDuration(500) // slow fade
                                .start()
                        }, 5000) // 5 sec = 5000 ms
                    }
                    .start()

                isExpanded = false
                isSleeping = true
            }
            .start()
    }


    private fun enableDrag(view: View) {
        var dX = 0f
        var dY = 0f
        var isDragging = false
        var startX = 0f
        var startY = 0f

        view.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    dX = v.x - event.rawX
                    dY = v.y - event.rawY
                    startX = event.rawX
                    startY = event.rawY
                    isDragging = false
                }
                MotionEvent.ACTION_MOVE -> {
                    val moveX = kotlin.math.abs(event.rawX - startX)
                    val moveY = kotlin.math.abs(event.rawY - startY)

                    if (moveX > 10 || moveY > 10) {
                        isDragging = true
                    }

                    if (isDragging) {
                        v.animate()
                            .x(event.rawX + dX)
                            .y(event.rawY + dY)
                            .setDuration(0)
                            .start()
                    }
                }
                MotionEvent.ACTION_UP -> {
                    if (!isDragging && !isExpanded) {
                        expandPopup()
                    } else {
                        val screenWidth = resources.displayMetrics.widthPixels
                        val middle = screenWidth / 2
                        val newX = if (v.x + v.width / 2 < middle) {
                            0f + 16f
                        } else {
                            (screenWidth - v.width).toFloat() - 16f
                        }
                        v.animate()
                            .x(newX)
                            .setDuration(300)
                            .start()
                    }
                }
            }
            true
        }
    }
}
