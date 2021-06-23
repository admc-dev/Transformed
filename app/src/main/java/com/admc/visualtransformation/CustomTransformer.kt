import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class CustomTransformer(
    private val delim: Char = '-',
    private val char: Char = 'X',
    format: String = "XXXX-XXXX-XXXX-XXXX"
) : VisualTransformation {

    private var max = 0
    private var delims = arrayListOf<Int>()

    init {
        max = format.count { it == char }

        format.forEachIndexed { index, c ->
            if (c == delim) {
                delims.add(index - delims.size)
            }
        }
    }

    override fun filter(text: AnnotatedString): TransformedText {

        val trimmed =
            if (text.text.length >= max) {
                text.text.substring(
                    0 until max
                )
            } else {
                text.text
            }

        var out = ""

        for (i in trimmed.indices) {
            out += trimmed[i]

            if (delims.contains(i + 1) && i != max - 1) out += "-"

        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                return ott(offset)
            }

            override fun transformedToOriginal(offset: Int): Int {
                return tto(offset)
            }
        }

        return TransformedText(AnnotatedString(out), offsetMapping)
    }

    //original to transformed
    private fun ott(offset: Int): Int {

        var offsetAddition = 0

        delims.forEach { delimIndex ->

            if (offset < delimIndex) {
                return offset + offsetAddition
            } else {
                offsetAddition++
            }
        }

        return max + offsetAddition
    }

    //transformed to original
    private fun tto(offset: Int): Int {

        var offsetMinus = 0

        delims.forEach { delimIndex ->

            if (offset <= delimIndex) {
                return offset - offsetMinus
            } else {
                offsetMinus--
            }
        }

        return max
    }
}