package com.firstsetup.myapplication

class AiAssistant {

    interface Callback {
        fun onAiRespond(message: String, options: List<String>)
    }

    private var state = 0
    private var selectedCategory = ""

    fun startConversation(callback: Callback) {
        callback.onAiRespond(
            "👋 Salut ! Bonjour ! Comment ça va ?",
            listOf("Salut", "Bonjour", "Comment ça va ?")
        )
    }

    fun handleUserChoice(choice: String, callback: Callback) {
        when (state) {
            0 -> {
                state = 1
                callback.onAiRespond(
                    "🤖 Je suis ravi de vous aider aujourd'hui ! Sur quel sujet voulez-vous qu'on travaille ?",
                    listOf(
                        "Gestion de ferme",
                        "Gestion des cultures",
                        "Gestion des animaux",
                        "Gestion de la météo"
                    )
                )
            }
            1 -> {
                selectedCategory = choice
                state = 2
                when (choice) {
                    "Gestion de ferme" -> {
                        callback.onAiRespond(
                            "🏡 Gestion de la ferme : Je peux vous aider à organiser vos parcelles, vos équipements, vos stocks et votre personnel. De quoi avez-vous besoin ?",
                            listOf("Organiser les tâches", "Gérer les équipements", "Retour")
                        )
                    }
                    "Gestion des cultures" -> {
                        callback.onAiRespond(
                            "🌾 Gestion des cultures : Voulez-vous que je vous aide sur les maladies, les conseils d'irrigation ou les périodes de plantation ?",
                            listOf("Détecter maladies", "Conseils irrigation", "Retour")
                        )
                    }
                    "Gestion des animaux" -> {
                        callback.onAiRespond(
                            "🐄 Gestion des animaux : Voulez-vous suivre la santé, la reproduction ou l'alimentation de vos animaux ?",
                            listOf("Suivi santé", "Gestion alimentation", "Retour")
                        )
                    }
                    "Gestion de la météo" -> {
                        callback.onAiRespond(
                            "☁️ Gestion de la météo : Voulez-vous recevoir des alertes météo, des prévisions agricoles, ou des conseils selon la saison ?",
                            listOf("Recevoir alertes", "Conseils saisonniers", "Retour")
                        )
                    }
                }
            }
            2 -> {
                when (choice) {
                    "Retour" -> {
                        state = 1
                        callback.onAiRespond(
                            "🔙 D'accord, choisissez à nouveau une catégorie.",
                            listOf(
                                "Gestion de ferme",
                                "Gestion des cultures",
                                "Gestion des animaux",
                                "Gestion de la météo"
                            )
                        )
                    }
                    else -> {
                        state = 3
                        callback.onAiRespond(
                            "✅ Super choix dans \"$selectedCategory\" ! Voulez-vous approfondir ou terminer la session ?",
                            listOf("Approfondir", "Terminer")
                        )
                    }
                }
            }
            3 -> {
                when (choice) {
                    "Approfondir" -> {
                        state = 2
                        callback.onAiRespond(
                            "💬 D'accord, précisez ce que vous voulez approfondir dans \"$selectedCategory\".",
                            listOf("Retour")
                        )
                    }
                    "Terminer" -> {
                        state = -1
                        callback.onAiRespond(
                            "🙏 Merci d'avoir utilisé notre Assistant Agricole. Bonne continuation dans vos projets agricoles ! 🌱",
                            emptyList() // No more options, conversation ends
                        )
                    }
                }
            }
            else -> {
                // Conversation finished
            }
        }
    }
}
