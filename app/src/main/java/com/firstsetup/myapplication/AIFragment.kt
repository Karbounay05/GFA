package com.firstsetup.myapplication

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firstsetup.myapplication.model.Message
class AIFragment : Fragment() {

    private lateinit var card: CardView
    private lateinit var aiBubble: ImageView
    private lateinit var overlayBackground: View
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ChatAdapter
    private lateinit var inputEditText: EditText
    private lateinit var sendButton: ImageButton

    private val messages = mutableListOf<Message>()
    private val aiAssistant = AiAssistant()

    private var isSleeping = false
    var isExpanded = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_ai, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("AIFragment", "🟢 Fragment chargé")
        card = view.findViewById(R.id.ai_card)
        aiBubble = view.findViewById(R.id.ai_bubble)
        overlayBackground = view.findViewById(R.id.overlay_background)
        recyclerView = view.findViewById(R.id.recyclerViewChat)
        inputEditText = view.findViewById(R.id.inputEditText)
        sendButton = view.findViewById(R.id.sendButton)

        val closeButton = view.findViewById<ImageButton>(R.id.closeButton)
        val minimizeButton = view.findViewById<ImageButton>(R.id.minimizeButton)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = ChatAdapter(messages) { userChoice ->
            simulateTyping(userChoice)
        }
        recyclerView.adapter = adapter

        sendButton.setOnClickListener {
            val userText = inputEditText.text.toString().trim()

            if (userText.isNotEmpty()) {
                messages.add(Message(userText, isUser = true))
                adapter.notifyItemInserted(messages.size - 1)
                recyclerView.scrollToPosition(messages.size - 1)
                inputEditText.text.clear()

                val typingMessage = Message("Assistant écrit...", isUser = false)
                messages.add(typingMessage)
                adapter.notifyItemInserted(messages.size - 1)
                recyclerView.scrollToPosition(messages.size - 1)

                aiAssistant.askQuestion(requireContext(), userText, object : AiAssistant.Callback {
                    override fun onAiRespond(message: String) {
                        val index = messages.indexOf(typingMessage)
                        if (index != -1) {
                            messages.removeAt(index)
                            adapter.notifyItemRemoved(index)
                        }

                        messages.add(Message(message, isUser = false))
                        adapter.notifyItemInserted(messages.size - 1)
                        recyclerView.scrollToPosition(messages.size - 1)
                    }
                })
            }
            Log.d("AIFragment", "📨 Message envoyé : $userText")
            Log.d("AIFragment", "📊 Nb messages maintenant: ${messages.size}")

        }

        closeButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        minimizeButton.setOnClickListener {
            minimizePopup()
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
                    requireActivity().supportFragmentManager.popBackStack()
                }
            }
            true
        }

        enableDrag(aiBubble)
    }

    private fun simulateTyping(userChoice: String) {
        val userMessage = Message(userChoice, isOption = false, isUser = true)
        messages.add(userMessage)
        adapter.notifyItemInserted(messages.size - 1)
        recyclerView.scrollToPosition(messages.size - 1)

        val typingMessage = Message("Assistant écrit...", isUser = false)
        messages.add(typingMessage)
        adapter.notifyItemInserted(messages.size - 1)
        recyclerView.scrollToPosition(messages.size - 1)

        aiAssistant.askQuestion(requireContext(), userChoice, object : AiAssistant.Callback {
            override fun onAiRespond(message: String) {
                val index = messages.indexOf(typingMessage)
                if (index != -1) {
                    messages.removeAt(index)
                    adapter.notifyItemRemoved(index)
                }

                messages.add(Message(message, isUser = false))
                adapter.notifyItemInserted(messages.size - 1)
                recyclerView.scrollToPosition(messages.size - 1)
            }
        })
    }


    fun expandPopup() {
        if (isSleeping) {
            aiBubble.animate().alpha(1f).setDuration(200).start()
            isSleeping = false
        }

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
                card.animate().scaleX(1f).scaleY(1f).alpha(1f).setDuration(300).start()
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
                    .scaleX(1f).scaleY(1f).alpha(1f)
                    .setDuration(300)
                    .withEndAction {
                        aiBubble.postDelayed({
                            aiBubble.animate().alpha(0.5f).setDuration(500).start()
                        }, 5000)
                    }.start()

                isExpanded = false
                isSleeping = true
            }.start()
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
                    if (moveX > 10 || moveY > 10) isDragging = true
                    if (isDragging) {
                        v.animate().x(event.rawX + dX).y(event.rawY + dY).setDuration(0).start()
                    }
                }
                MotionEvent.ACTION_UP -> {
                    if (!isDragging && !isExpanded) {
                        expandPopup()
                    } else {
                        val screenWidth = resources.displayMetrics.widthPixels
                        val middle = screenWidth / 2
                        val newX = if (v.x + v.width / 2 < middle) 0f + 16f else (screenWidth - v.width).toFloat() - 16f
                        v.animate().x(newX).setDuration(300).start()
                    }
                }
            }
            true
        }
    }
}
