<script setup lang="ts">
import { reactive, ref } from 'vue'

const currencies = ref([])

fetch('/api/currencies')
    .then((response) => response.json())
    .then((data) => (currencies.value = data))
    .catch((err) => console.error(err))
</script>

<template>
    <div>{{ currencies }}</div>
</template>
