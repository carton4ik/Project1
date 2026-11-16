package com.example.project1 // Твой пакет!

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

data class Block(val type: String, val value: Int)

class MainActivity : AppCompatActivity() {

    private var playerName = "Игрок"

    private var currentLevel = 1
    private var coins = 0
    private var diamonds = 0

    private var energy = 10
    private var maxEnergy = 10
    private var pickaxeLevel = 1
    private var upgradeCost = 100

    private var hasMegaPickaxe = false
    private var hasInfiniteEnergy = false

    private var gridSize = 3
    private var totalBlocks = gridSize * gridSize

    private lateinit var rewards: Array<Block>
    private lateinit var opened: BooleanArray

    // UI Главное
    private lateinit var menuLayout: LinearLayout
    private lateinit var mainGameLayout: LinearLayout
    private lateinit var inputName: EditText
    private lateinit var playerNameTitle: TextView

    // UI Игра
    private lateinit var coinsText: TextView
    private lateinit var energyText: TextView
    private lateinit var levelText: TextView
    private lateinit var statusText: TextView
    private lateinit var diamondText: TextView
    private lateinit var grid: GridLayout
    private lateinit var btnNextLevel: Button

    // UI Улучшения
    private lateinit var btnUpgradeCoins: Button
    private lateinit var btnMegaPickaxe: Button
    private lateinit var btnInfiniteEnergy: Button

    // Вкладки
    private lateinit var tabGame: LinearLayout
    private lateinit var tabCollection: LinearLayout
    private lateinit var tabSettings: ScrollView
    private lateinit var btnTabGame: Button
    private lateinit var btnTabCollection: Button
    private lateinit var btnTabSettings: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Экраны
        menuLayout = findViewById(R.id.menuLayout)
        mainGameLayout = findViewById(R.id.mainGameLayout)
        inputName = findViewById(R.id.inputName)
        playerNameTitle = findViewById(R.id.playerNameTitle)

        findViewById<Button>(R.id.btnStartGame).setOnClickListener {
            val name = inputName.text.toString().trim()
            if (name.isNotEmpty()) {
                playerName = name
            }
            playerNameTitle.text = playerName
            // Переключаем экраны
            menuLayout.visibility = View.GONE
            mainGameLayout.visibility = View.VISIBLE
            startLevel()
        }

        // Игровые элементы
        coinsText = findViewById(R.id.coinsText)
        energyText = findViewById(R.id.energyText)
        levelText = findViewById(R.id.levelText)
        statusText = findViewById(R.id.statusText)
        diamondText = findViewById(R.id.diamondText)
        grid = findViewById(R.id.grid)
        btnNextLevel = findViewById(R.id.btnNextLevel)

        btnUpgradeCoins = findViewById(R.id.btnUpgradeCoins)
        btnMegaPickaxe = findViewById(R.id.btnMegaPickaxe)
        btnInfiniteEnergy = findViewById(R.id.btnInfiniteEnergy)

        tabGame = findViewById(R.id.tabGame)
        tabCollection = findViewById(R.id.tabCollection)
        tabSettings = findViewById(R.id.tabSettings)

        btnTabGame = findViewById(R.id.btnTabGame)
        btnTabCollection = findViewById(R.id.btnTabCollection)
        btnTabSettings = findViewById(R.id.btnTabSettings)

        findViewById<Button>(R.id.btnReset).setOnClickListener { resetGame() }
        btnNextLevel.setOnClickListener { nextLevel() }

        btnUpgradeCoins.setOnClickListener { upgradePickaxe() }
        btnMegaPickaxe.setOnClickListener { buyMegaPickaxe() }
        btnInfiniteEnergy.setOnClickListener { buyInfiniteEnergy() }

        btnTabGame.setOnClickListener { switchTab(0) }
        btnTabCollection.setOnClickListener { switchTab(1) }
        btnTabSettings.setOnClickListener { switchTab(2) }
    }

    // ... (Остальные функции switchTab, updateUpgradeUI, upgradePickaxe, buyMegaPickaxe,
    // buyInfiniteEnergy, startLevel, nextLevel, dig, openBlock, getNeighborIndexes, updateUI
    // остаются БЕЗ ИЗМЕНЕНИЙ, как в предыдущем сообщении) ...

    private fun switchTab(index: Int) {
        tabGame.visibility = View.GONE
        tabCollection.visibility = View.GONE
        tabSettings.visibility = View.GONE

        val activeBg = Color.parseColor("#3D2B1F")
        val inactiveBg = Color.parseColor("#1C1C26")

        btnTabGame.setBackgroundColor(inactiveBg)
        btnTabGame.setTextColor(Color.parseColor("#888888"))
        btnTabCollection.setBackgroundColor(inactiveBg)
        btnTabCollection.setTextColor(Color.parseColor("#888888"))
        btnTabSettings.setBackgroundColor(inactiveBg)
        btnTabSettings.setTextColor(Color.parseColor("#888888"))

        when (index) {
            0 -> {
                tabGame.visibility = View.VISIBLE
                btnTabGame.setBackgroundColor(activeBg)
                btnTabGame.setTextColor(Color.WHITE)
            }
            1 -> {
                tabCollection.visibility = View.VISIBLE
                btnTabCollection.setBackgroundColor(activeBg)
                btnTabCollection.setTextColor(Color.WHITE)
                diamondText.text = "💎 Алмазов: $diamonds"
            }
            2 -> {
                tabSettings.visibility = View.VISIBLE
                btnTabSettings.setBackgroundColor(activeBg)
                btnTabSettings.setTextColor(Color.WHITE)
                updateUpgradeUI()
            }
        }
    }

    private fun updateUpgradeUI() {
        if (pickaxeLevel >= 5) {
            btnUpgradeCoins.isEnabled = false
            btnUpgradeCoins.text = "Максимальный уровень кирки"
            btnUpgradeCoins.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#333333"))
        } else {
            btnUpgradeCoins.text = "Улучшить кирку (Ур. $pickaxeLevel)\nЦена: $upgradeCost 🪙"
            if (coins >= upgradeCost) {
                btnUpgradeCoins.isEnabled = true
                btnUpgradeCoins.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#3D2B1F"))
            } else {
                btnUpgradeCoins.isEnabled = false
                btnUpgradeCoins.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#333333"))
            }
        }

        if (hasMegaPickaxe) {
            btnMegaPickaxe.isEnabled = false
            btnMegaPickaxe.text = "Мега-кирка куплена"
            btnMegaPickaxe.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#333333"))
        } else {
            btnMegaPickaxe.isEnabled = diamonds >= 10
            btnMegaPickaxe.backgroundTintList = ColorStateList.valueOf(
                if (diamonds >= 10) Color.parseColor("#2A2A3A") else Color.parseColor("#333333")
            )
        }

        if (hasInfiniteEnergy) {
            btnInfiniteEnergy.isEnabled = false
            btnInfiniteEnergy.text = "Бесконечная энергия куплена"
            btnInfiniteEnergy.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#333333"))
        } else {
            btnInfiniteEnergy.isEnabled = diamonds >= 20
            btnInfiniteEnergy.backgroundTintList = ColorStateList.valueOf(
                if (diamonds >= 20) Color.parseColor("#2A2A3A") else Color.parseColor("#333333")
            )
        }
    }

    private fun upgradePickaxe() {
        if (coins >= upgradeCost && pickaxeLevel < 5) {
            coins -= upgradeCost
            pickaxeLevel++
            upgradeCost *= 2
            maxEnergy += 5
            if (!hasInfiniteEnergy) energy = maxEnergy

            Toast.makeText(this, "Кирка улучшена!", Toast.LENGTH_SHORT).show()
            updateUI()
            updateUpgradeUI()
        }
    }

    private fun buyMegaPickaxe() {
        if (diamonds >= 10 && !hasMegaPickaxe) {
            diamonds -= 10
            hasMegaPickaxe = true
            Toast.makeText(this, "Мега-кирка куплена!", Toast.LENGTH_SHORT).show()
            updateUpgradeUI()
            updateUI()
        }
    }

    private fun buyInfiniteEnergy() {
        if (diamonds >= 20 && !hasInfiniteEnergy) {
            diamonds -= 20
            hasInfiniteEnergy = true
            energyText.text = "⚡ ∞"
            Toast.makeText(this, "Энергия теперь бесконечна!", Toast.LENGTH_SHORT).show()
            updateUpgradeUI()
            updateUI()
        }
    }

    private fun startLevel() {
        gridSize = when {
            currentLevel < 5 -> 3
            currentLevel < 10 -> 4
            else -> 5
        }
        totalBlocks = gridSize * gridSize
        grid.columnCount = gridSize
        grid.rowCount = gridSize

        rewards = Array(totalBlocks) {
            val roll = Random.nextInt(100)
            val mult = 1 + (currentLevel / 5)

            when {
                roll < 50 -> Block("coin", Random.nextInt(5, 15) * mult)
                roll < 65 -> Block("diamond", 1)
                roll < 75 -> Block("chest", 0)
                else -> Block("empty", 0)
            }
        }
        opened = BooleanArray(totalBlocks) { false }

        if (!hasInfiniteEnergy) energy = maxEnergy

        levelText.text = "Уровень $currentLevel"
        statusText.text = "Копай!"
        statusText.setTextColor(Color.parseColor("#888888"))
        btnNextLevel.visibility = View.GONE

        updateUI()
    }

    private fun nextLevel() {
        currentLevel++
        startLevel()
    }

    private fun dig(index: Int) {
        if (opened[index]) return

        if (energy <= 0 && !hasInfiniteEnergy) {
            Toast.makeText(this, "Нет энергии!", Toast.LENGTH_SHORT).show()
            return
        }

        var blocksBroken = 0
        openBlock(index)
        if (!hasInfiniteEnergy) energy--
        blocksBroken++

        if (hasMegaPickaxe) {
            val neighbors = getNeighborIndexes(index).filter { !opened[it] }
            val toBreak = neighbors.shuffled().take(2)
            for (nIndex in toBreak) {
                openBlock(nIndex)
                blocksBroken++
            }
        } else {
            val extraChance = (pickaxeLevel - 1) * 15
            if (Random.nextInt(100) < extraChance) {
                val neighbors = getNeighborIndexes(index).filter { !opened[it] }
                if (neighbors.isNotEmpty()) {
                    openBlock(neighbors.random())
                    blocksBroken++
                }
            }
        }

        if (blocksBroken > 1) {
            statusText.text = "Сломано блоков: $blocksBroken!"
            statusText.setTextColor(Color.parseColor("#7CFFB2"))
        }

        if (opened.all { it }) {
            btnNextLevel.visibility = View.VISIBLE
        }

        updateUI()
    }

    private fun openBlock(index: Int) {
        if (opened[index]) return
        opened[index] = true
        val block = rewards[index]

        when (block.type) {
            "coin" -> {
                coins += block.value
                if (!statusText.text.contains("блоков")) {
                    statusText.text = "🪙 +${block.value}"
                    statusText.setTextColor(Color.parseColor("#FFD700"))
                }
            }
            "diamond" -> {
                diamonds += block.value
                if (!statusText.text.contains("блоков")) {
                    statusText.text = "💎 +${block.value}"
                    statusText.setTextColor(Color.parseColor("#7CFFB2"))
                }
            }
            "chest" -> {
                if (Random.nextBoolean()) {
                    coins += 50
                    if (!statusText.text.contains("блоков")) {
                        statusText.text = "🎁 +50 монет!"
                        statusText.setTextColor(Color.parseColor("#FFD700"))
                    }
                } else {
                    diamonds += 3
                    if (!statusText.text.contains("блоков")) {
                        statusText.text = "🎁 +3 алмаза!"
                        statusText.setTextColor(Color.parseColor("#7CFFB2"))
                    }
                }
            }
            "empty" -> {
                if (!statusText.text.contains("блоков")) {
                    statusText.text = "Пусто..."
                    statusText.setTextColor(Color.parseColor("#888888"))
                }
            }
        }
    }

    private fun getNeighborIndexes(index: Int): List<Int> {
        val list = mutableListOf<Int>()
        val row = index / gridSize
        val col = index % gridSize
        if (row > 0) list.add(index - gridSize)
        if (row < gridSize - 1) list.add(index + gridSize)
        if (col > 0) list.add(index - 1)
        if (col < gridSize - 1) list.add(index + 1)
        return list
    }

    private fun updateUI() {
        coinsText.text = "🪙 $coins"
        if (hasInfiniteEnergy) {
            energyText.text = "⚡ ∞"
        } else {
            energyText.text = "⚡ $energy/$maxEnergy"
        }

        grid.removeAllViews()
        val screenWidth = resources.displayMetrics.widthPixels
        val cellSize = (screenWidth / gridSize) - 32

        for (i in 0 until totalBlocks) {
            val cell = TextView(this).apply {
                val lp = GridLayout.LayoutParams().apply {
                    width = cellSize
                    height = cellSize
                    setMargins(12, 12, 12, 12)
                }
                layoutParams = lp
                gravity = Gravity.CENTER
                textSize = if (gridSize > 3) 18f else 24f
                setTypeface(null, Typeface.BOLD)

                if (opened[i]) {
                    when (rewards[i].type) {
                        "coin" -> {
                            text = "🪙\n+${rewards[i].value}"
                            setTextColor(Color.parseColor("#FFD700"))
                        }
                        "diamond" -> {
                            text = "💎\n+${rewards[i].value}"
                            setTextColor(Color.parseColor("#7CFFB2"))
                        }
                        "chest" -> {
                            text = "🎁\nБОНУС"
                            setTextColor(Color.parseColor("#FFD700"))
                        }
                        else -> {
                            text = "🪨"
                            setTextColor(Color.parseColor("#555555"))
                        }
                    }
                    background = GradientDrawable().apply {
                        setColor(Color.parseColor("#1C1C26"))
                        cornerRadius = 20f
                    }
                } else {
                    text = "⛏️"
                    setTextColor(Color.WHITE)
                    background = GradientDrawable().apply {
                        setColor(Color.parseColor("#2A2A3A"))
                        cornerRadius = 20f
                        setStroke(4, Color.parseColor("#3D2B1F"))
                    }
                }
                setOnClickListener { dig(i) }
            }
            grid.addView(cell)
        }
    }

    private fun resetGame() {
        currentLevel = 1
        coins = 0
        diamonds = 0
        pickaxeLevel = 1
        upgradeCost = 100
        maxEnergy = 10
        energy = 10
        hasMegaPickaxe = false
        hasInfiniteEnergy = false
        startLevel()
        switchTab(0)
        Toast.makeText(this, "Прогресс сброшен", Toast.LENGTH_SHORT).show()
    }
}