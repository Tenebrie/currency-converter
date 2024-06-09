<script setup lang="ts">
import { getLocale } from '@/utils/getLocale'
import type { ApiConversionResult, ApiCurrencyList } from '@/utils/types'
import { ref } from 'vue'
import ErrorNotification from './ErrorNotification.vue'

const errorMessage = ref<string>('')
const isErrorVisible = ref<boolean>(false)
const onHideError = () => (isErrorVisible.value = false)
const showError = (message: string) => {
    errorMessage.value = message
    isErrorVisible.value = true
}

const currencies = ref<string[]>([])

const saveCurrencies = (data: ApiCurrencyList) => {
    currencies.value = data.sort((a, b) => a.localeCompare(b))
}

const sourceCurrency = ref<string>('EUR')
const targetCurrency = ref<string>('USD')
const swap = () => {
    ;[sourceCurrency.value, targetCurrency.value] = [targetCurrency.value, sourceCurrency.value]
}

const isFetchingInitial = ref<boolean>(true)
fetch('/api/currencies')
    .then((response) => response.json())
    .then((data) => saveCurrencies(data as ApiCurrencyList))
    .catch((err) => {
        console.error(err)
        showError(err)
    })
    .finally(() => (isFetchingInitial.value = false))

const inputValidation = [
    (value: string) => value == '' || !isNaN(parseFloat(value)) || 'Must be a number.'
]

const inputAmount = ref<number>(0)
const convertedAmount = ref<string>('')
const isFetchingConversion = ref<boolean>(false)

const formatConvertedValue = (result: ApiConversionResult) => {
    const formattedValue = new Intl.NumberFormat(getLocale(), {
        style: 'currency',
        currency: result.targetCurrency
    }).format(result.convertedValue)

    convertedAmount.value = formattedValue
}

const convert = () => {
    if (isFetchingConversion.value) {
        return
    }

    isFetchingConversion.value = true

    const query = new URLSearchParams({
        source: sourceCurrency.value,
        target: targetCurrency.value,
        value: String(inputAmount.value)
    })
    fetch('/api/convert?' + query)
        .then((response) => response.json())
        .then((value) => formatConvertedValue(value as ApiConversionResult))
        .catch((err) => {
            console.error(err)
            showError(err)
        })
        .finally(() => (isFetchingConversion.value = false))
}
</script>

<template>
    <div class="container">
        <v-sheet class="sheet">
            <div class="section">
                <v-text-field
                    v-model="inputAmount"
                    width="1"
                    :rules="inputValidation"
                    label="Monetary value"
                />
                <v-btn @click="convert" :loading="isFetchingConversion" color="primary">
                    <v-icon icon="mdi-swap-horizontal" />
                    Convert
                </v-btn>
                <v-text-field
                    v-model="convertedAmount"
                    width="1"
                    label="Converted value"
                    readonly
                />
            </div>
            <v-divider />
            <div class="section">
                <v-select
                    width="2"
                    label="Source currency"
                    v-model="sourceCurrency"
                    :items="currencies"
                    :loading="isFetchingInitial"
                />
                <v-btn @click="swap" color="secondary" width="1">
                    <v-icon icon="mdi-swap-horizontal" />
                </v-btn>
                <v-select
                    width="2"
                    label="Target currency"
                    v-model="targetCurrency"
                    :items="currencies"
                    :loading="isFetchingInitial"
                />
            </div>
        </v-sheet>
        <ErrorNotification
            :is-opened="isErrorVisible"
            :message="errorMessage"
            @some-event="onHideError"
        />
    </div>
</template>

<style scoped>
.container {
    width: 720px;
}

.section {
    display: flex;
    align-items: center;
    justify-content: center;
    flex-direction: row;
    width: 100%;
    height: 100%;
    gap: 8px;
}

.sheet {
    padding: 32px;
    display: flex;
    justify-content: center;
    flex-direction: column;
    gap: 8px;
}
</style>
