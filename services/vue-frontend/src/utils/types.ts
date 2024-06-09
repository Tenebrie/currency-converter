export type ApiCurrencyList = string[]

export type ApiConversionResult = {
    sourceCurrency: string
    targetCurrency: string
    exchangeRate: number
    originalValue: number
    convertedValue: number
    operationDate: string
}
