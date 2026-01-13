package com.fishscal.plisfo.data.model

data class FishType(
    val name: String,
    val icon: String = "🐟"
)

val defaultFishTypes = listOf(
    FishType("Bass", "🐟"),
    FishType("Trout", "🐟"),
    FishType("Pike", "🐟"),
    FishType("Carp", "🐟"),
    FishType("Catfish", "🐟"),
    FishType("Salmon", "🐟"),
    FishType("Perch", "🐟"),
    FishType("Walleye", "🐟"),
    FishType("Bluegill", "🐟"),
    FishType("Crappie", "🐟"),
    FishType("Muskie", "🐟"),
    FishType("Other", "🐟")
)

