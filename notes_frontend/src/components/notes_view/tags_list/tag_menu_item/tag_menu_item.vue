<template>
  <div id="container">
    <div id="left">
      <p @click="$emit('select')">{{ itemName }}</p>
    </div>
    <div id="right">
      <button @click="$emit('delete')">delete</button>
    </div>
  </div>
</template>

<script>
  export default {
    name: 'TagMenuItem',
    props: ['itemName'],
    data() {
      return {
        newTagName: '',
        tags: []
      }
    }
  }
</script>

<style scoped>
  #container {
    width: 100%;
    border: #000088 solid;
  }

  #left, #right {
    display: inline-block;
    *display: inline;
    vertical-align: center;
    font-size: 11px;
    cursor: pointer;
  }

  #left {
    width: 68%;
  }

  #right {
    width: 28%;
  }

  p {
    padding: 6px 8px 6px 16px;
    text-decoration: none;
    font-size: 25px;
    color: #818181;
    display: block;
  }

  /* When you mouse over the navigation links, change their color */
  p:hover {
    color: #f1f1f1;
  }
</style>
