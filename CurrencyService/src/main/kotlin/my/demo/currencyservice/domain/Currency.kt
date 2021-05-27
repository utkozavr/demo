package my.demo.currencyservice.domain

interface Currency {
    val code: String
    val name: String
    val isActive: Boolean
    val isDisabledManually: Boolean

    fun getCopy(
        code: String = this.code,
        name: String = this.name,
        isActive: Boolean = this.isActive,
        isDisabledManually: Boolean = this.isDisabledManually
    ): Currency
}