package sefaz.ms.produtos.app

import javafx.scene.paint.Color
import javafx.scene.paint.Color.*
import javafx.scene.paint.CycleMethod
import javafx.scene.paint.LinearGradient
import javafx.scene.text.FontWeight
import javafx.scene.paint.Stop
import tornadofx.*
import javafx.scene.text.FontWeight.BOLD
import tornadofx.FX.Companion.icon


class Styles : Stylesheet() {
    companion object {
        val heading by cssclass()
        val successButton by cssclass()
        val icon by cssclass()
    }

    init {
        label and heading {
            padding = box(10.px)
            fontSize = 20.px
            fontWeight = FontWeight.BOLD
        }

        successButton {
            borderRadius += box(4.px)
            padding = box(8.px, 15.px)
            backgroundInsets += box(0.px)
            borderColor += box(c("#5ca941"))
            textFill = Color.WHITE
            fontWeight = BOLD
            backgroundColor += LinearGradient(0.0, 0.0, 0.0, 1.0, true, CycleMethod.NO_CYCLE, Stop(0.0, c("#8add6d")), Stop(1.0, c("#60b044")))
            and(hover) {
                backgroundColor += LinearGradient(0.0, 0.0, 0.0, 1.0, true, CycleMethod.NO_CYCLE, Stop(0.0, c("#79d858")), Stop(1.0, c("#569e3d")))
            }
            and(pressed) {
                backgroundColor += c("#569e3d")
            }
            icon {
                backgroundColor += WHITE
            }
        }

    }
}