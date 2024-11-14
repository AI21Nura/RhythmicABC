# Rhythmic ABC

*Rhythmic ABC* is an app based on the rhythmic alphabet, a system developed by renowned drummer and educator **Benny Greb** for improving rhythmic skills.

### What is the Rhythmic Alphabet?

The *Rhythmic Alphabet* is a unique system where each letter of the alphabet corresponds to a specific rhythmic pattern. This method allows musicians to study rhythms in a structured way and explore their diversity through familiar symbols. By associating rhythms with letters, drummers and percussionists can creatively work with rhythm, develop a sense of timing, and memorize patterns more effectively.

![](https://static.wixstatic.com/media/8ce0b4_02931e514eac4ed48c10cf9c28aec8f9~mv2.png/v1/fill/w_556,h_808,al_c,q_90,usm_0.66_1.00_0.01,enc_auto/rhythmic_alphabet.png)
*Image taken from [Drum Academy](https://www.drumacademy.de/)*

---

### Functional Requirements (To-Do)

- [x] Display rhythmic alphabet grid
- [x] Show pattern structure for each letter
- [x] Play rhythmic pattern for each letter
- [x] Highlight currently playing pattern element
- [ ] Play the full alphabet sequence
- [ ] Playback control (Start/Stop)
- [ ] BPM control with current value display

---

### Technology Stack and Implementation

- **UI**: Jetpack Compose for UI creation
- **Audio**: `SoundPool` class for efficient playback of short audio patterns
- **DI**: Manual dependency injection within the app container
- **Architecture**: Single-module project structure for easier development
- **Coroutines**: Using Kotlin Coroutines for managing audio
