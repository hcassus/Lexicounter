# Build Status

[![Codeship Status for hcassus/Lexicounter](https://app.codeship.com/projects/75e060c0-28d2-0138-3533-5e40735de613/status?branch=master)](https://app.codeship.com/projects/383914)

# Requirements to use the word counter

- JDK 11
- Suitable `CharacterReader`

# Usage

You can choose whether to use `ParallelWordCounter` with as many `CharacterReaders` you want, or use `SerialWordCounter` 
if you intent to use a single `CharacterReader`.

`ParallelWordCounter` will output the progress of the counts at every 10 seconds and return the final result in the end, 
while `SerialWordCounter` will only return the ordered map with the counts.

# Special treatments

- Composite words and contractions like `thirty-six` and `they're` are counted as a single word in order not to strip 
the aggregated words from their original context, making it easier to gain insights on their frequency.