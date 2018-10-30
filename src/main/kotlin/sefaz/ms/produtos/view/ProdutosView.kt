package sefaz.ms.produtos.view

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.TableView
import javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode
import javafx.scene.layout.Priority
import org.controlsfx.control.CheckComboBox
import org.controlsfx.control.Notifications
import sefaz.ms.produtos.app.Styles.Companion.successButton
import sefaz.ms.produtos.model.ProdutoSefaz
import sefaz.ms.produtos.repository.ProdutosRepository
import tornadofx.*

class ProdutosView : View("ProdutosApp") {

    private val produtos: ObservableList<ProdutoSefaz>
    private var codigosDePodutos: ObservableList<Int>
    private var fatoresConv: ObservableList<Int>

    private val editedRows = HashMap<Int, Pair<Int?, Int?>>()

    private var table: TableView<ProdutoSefaz> = TableView()

    private var data: SortedFilteredList<ProdutoSefaz>

    private lateinit var idTextField: TextField
    private lateinit var descrProdTextField: TextField
    private lateinit var unidTextField: TextField
    private lateinit var dtInsercaoTextField: TextField
    private lateinit var fatorConvCheckComboBox: CheckComboBox<Int>
    private lateinit var codigoProdutoSefazCheckComboBox: CheckComboBox<Int>

    private var codProdSefazChecked: ObservableList<Int>
    private var fatorConvChecked: ObservableList<Int>

    init {
        produtos = FXCollections.observableArrayList(ProdutosRepository.produtos())
        codigosDePodutos = FXCollections.observableArrayList(ProdutosRepository.codigosProdutos())
        fatoresConv = FXCollections.observableArrayList(ProdutosRepository.fatoresConv())
        codProdSefazChecked = FXCollections.observableArrayList()
        fatorConvChecked = FXCollections.observableArrayList()
        data = SortedFilteredList(produtos).bindTo(table)
    }

    private fun filter() {
        data.predicate = {

            var filterId = true
            var filterDescrProd = true
            var filterCodProdSefaz = true
            var filterUnid = true
            var filterFatoConv = true
            var filterDtInsercao = true


            if (idTextField.text.isNotEmpty()) {
                filterId = it.id.toString().contains(idTextField.text, true)
            }

            if (dtInsercaoTextField.text.isNotEmpty()) {
                filterDtInsercao = it.dtInsercao.toString().contains(dtInsercaoTextField.text, true)
            }

            if (descrProdTextField.text.isNotEmpty()) {
                filterDescrProd = it.descrProd.contains(descrProdTextField.text, true)
            }

            if (unidTextField.text.isNotEmpty()) {
                filterUnid = it.unid.contains(unidTextField.text, true)
            }

            if (codProdSefazChecked.size > 0) {
                filterCodProdSefaz = it.codProdSefaz in codProdSefazChecked
            }

            if (fatorConvChecked.size > 0) {
                filterFatoConv = it.fatorConv in fatorConvChecked
            }

            (filterId and filterDtInsercao and filterDescrProd
                    and filterUnid and filterCodProdSefaz and filterFatoConv
                    )
        }
    }

    override val root = vbox(10.0) {

        hbox(8.0) {
            button("Salvar Alterações") {
                action {

                    val editedRowsCopy = HashMap<Int, Pair<Int?, Int?>>(editedRows.size)

                    editedRowsCopy.putAll(editedRows)

                    val quantidadeRegistrosAlterados = editedRowsCopy.size

                    if (quantidadeRegistrosAlterados > 0) {

                        runAsync {
                            ProdutosRepository.salvarAlteraçoes(editedRowsCopy.toList())
                        } ui {
                            codigosDePodutos = FXCollections.observableArrayList(ProdutosRepository.codigosProdutos())
                            codigoProdutoSefazCheckComboBox.items.clear()
                            codigoProdutoSefazCheckComboBox.items.addAll(codigosDePodutos)

                            fatoresConv = FXCollections.observableArrayList(ProdutosRepository.fatoresConv())
                            fatorConvCheckComboBox.items.clear()
                            fatorConvCheckComboBox.items.addAll(fatoresConv)
                        }

                        val mensagem = if (quantidadeRegistrosAlterados == 1) {
                            "$quantidadeRegistrosAlterados registro está sendo salvo"
                        } else {
                            "$quantidadeRegistrosAlterados registros estão sendo salvos"
                        }

                        Notifications.create()
                                .text(mensagem)
                                .owner(this@vbox)
                                .position(Pos.TOP_RIGHT)
                                .showInformation()
                    } else {
                        Notifications.create()
                                .text("Você não alterou nenhum registro")
                                .owner(this@vbox)
                                .position(Pos.TOP_RIGHT)
                                .showWarning()
                    }
                    editedRows.clear()
                }
                addClass(successButton)
            }
        }

        table = tableview(data) {
            column("", ProdutoSefaz::id).graphic = vbox {
                label("ID")
                idTextField = textfield {
                    setOnKeyReleased {
                        filter()
                    }
                }
            }
            column("", ProdutoSefaz::dtInsercao).graphic = vbox {
                label("DT_INSERCAO")
                dtInsercaoTextField = textfield {
                    setOnKeyReleased {
                        filter()
                    }
                }
            }
            column("", ProdutoSefaz::unid).graphic = vbox {
                label("PROD_XPROD")
                unidTextField = textfield {
                    setOnKeyReleased {
                        filter()
                    }
                }
            }
            column("", ProdutoSefaz::descrProd).graphic = vbox {
                label("PROD_NCM")
                descrProdTextField = textfield {
                    setOnKeyReleased {
                        filter()
                    }
                }
            }

            val colFatorConv = column("", ProdutoSefaz::fatorConv)

            colFatorConv.graphic = vbox {
                label("FATOR_CONV")

                fatorConvCheckComboBox = CheckComboBox<Int>(fatoresConv)

                fatorConvCheckComboBox.maxWidth = colFatorConv.maxWidth

                add(fatorConvCheckComboBox)

                fatorConvCheckComboBox.checkModel.checkedItems.onChange {
                    fatorConvChecked = fatorConvCheckComboBox.checkModel.checkedItems
                    filter()

                }
                padding = Insets(1.0, 1.0, 5.0, 1.0)
            }

            colFatorConv.makeEditable().setOnEditCommit {

                val prod_id = it.rowValue.id
                val codProdSefazAtual = data.find { it.id == prod_id }?.codProdSefaz

                if (codProdSefazAtual != null) {
                    editedRows[it.rowValue.id] = Pair(it.newValue, codProdSefazAtual)
                } else {
                    editedRows[it.rowValue.id] = Pair(it.newValue, null)
                }

                it.rowValue.fatorConv = it.newValue
            }

            val colCodProdSefaz = column("", ProdutoSefaz::codProdSefaz)

            colCodProdSefaz.graphic = vbox {
                label("COD_PROD_SEFAZ")

                codigoProdutoSefazCheckComboBox = CheckComboBox<Int>(codigosDePodutos)

                codigoProdutoSefazCheckComboBox.maxWidth = colCodProdSefaz.maxWidth

                add(codigoProdutoSefazCheckComboBox)

                codigoProdutoSefazCheckComboBox.checkModel.checkedItems.onChange {
                    codProdSefazChecked = codigoProdutoSefazCheckComboBox.checkModel.checkedItems
                    filter()

                }
                padding = Insets(1.0, 1.0, 5.0, 1.0)
            }

            colCodProdSefaz.makeEditable().setOnEditCommit {

                val prod_id = it.rowValue.id
                val fatorConvAutal = data.find { it.id == prod_id }?.fatorConv

                if (fatorConvAutal != null) {
                    editedRows[it.rowValue.id] = Pair(fatorConvAutal, it.newValue)
                } else {
                    editedRows[it.rowValue.id] = Pair(null, it.newValue)
                }

                it.rowValue.codProdSefaz = it.newValue
            }

            setPrefSize(667.0, 376.0)
            columnResizePolicy = CONSTRAINED_RESIZE_POLICY
            vgrow = Priority.ALWAYS

            multiSelect(true)
            enableCellEditing()

            setOnKeyPressed { it ->
                if (it.code == KeyCode.SPACE) {
                    dialog {
                        vbox(6.0) {

                            label("Fator Conv")
                            val fatorConv = textfield()

                            label("Código de Produto")
                            val codigoProduto = textfield()

                            button("Preencher") {
                                action {

                                    val codigoNumero = codigoProduto.text.toIntOrNull()

                                    if (codigoNumero != null) {
                                        selectionModel.selectedItems
                                                .forEach {
                                                    it.codProdSefaz = codigoNumero
                                                    val fatorConvAutal = it.fatorConv
                                                    editedRows[it.id] = Pair(fatorConvAutal, codigoNumero)
                                                }
                                        refresh()
                                        close()
                                    } else {
                                        codigoProduto.text = ""
                                    }

                                    val fatorConvNumero = fatorConv.text.toIntOrNull()

                                    if (fatorConvNumero != null) {
                                        selectionModel.selectedItems
                                                .forEach {
                                                    it.fatorConv = fatorConvNumero
                                                    val codProdSefazAtual =it.codProdSefaz
                                                    editedRows[it.id] = Pair(fatorConvNumero, codProdSefazAtual)
                                                }
                                        refresh()
                                        close()
                                    } else {
                                        fatorConv.text = ""
                                    }
                                }
                                addClass(successButton)
                            }
                        }
                    }
                }
            }
        }
        padding = Insets(10.0)
    }
}