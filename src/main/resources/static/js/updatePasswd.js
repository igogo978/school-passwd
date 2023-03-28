export default {
  data() {
    return {
      passwd: ''
    }
  },
  props: {
    show: Boolean,
  },
  methods: {
    updatepasswd() {
      this.$emit('updatepasswd', this.passwd);
    },
    close() {
      this.$emit('updatepasswd', '');
    }

  },
  template: `
  <transition name="modal">
    <div v-if="show" class="modal-mask">


      <div class="modal-container">
        <div class="modal-header">
          <slot name="header">輸入新密碼</slot>
        </div>

        <div class="modal-body">
          <slot name="body">
		  <div class="input-group mb-3">
		  <input @input="event => passwd = event.target.value" type="password" class="form-control" placeholder="輸入新密碼" aria-label="輸入新密碼" aria-describedby="basic-addon1">
		  </div>

	  </slot>
        </div>

        <div class="modal-footer">
          <slot name="footer">
          <button type="button" class="btn" @click="close" >Close</button>
            
           <button type="button" class="btn btn-primary" @click="updatepasswd">
		    OK
	    </button>
          </slot>
        </div>
      </div>
    </div>
  </transition>
  `
}
