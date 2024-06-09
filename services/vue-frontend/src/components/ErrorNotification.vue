<script setup lang="ts">
import { ref, watch } from 'vue'

const props = defineProps<{
    isOpened: boolean
    message: string
}>()

const isOpened = ref(props.isOpened)
const message = ref(props.message)

watch(props, async (newProps) => {
    isOpened.value = newProps.isOpened
    message.value = newProps.message
})
</script>

<template>
    <v-snackbar v-model="isOpened" color="error"
        >{{ message }}
        <template v-slot:actions>
            <v-btn variant="text" @click="$emit('someEvent')"><v-icon icon="mdi-close" /></v-btn>
        </template>
    </v-snackbar>
</template>
