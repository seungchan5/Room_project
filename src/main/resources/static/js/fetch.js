function fetchGet(url, callback) {
    fetch(url)
    .then(res => res.json())
    .then(map => callback(map));
}

function fetchPost(url, json, callback) {
    fetch(url , { method : 'post'
					, headers : {'Content-Type' : 'application/json'}
					, body : JSON.stringify(json)
				})
    .then(res => res.json())
    .then(map => callback(map))
	.catch(error => console.log(error));
}

function fetchDelete(url, json, callback) {
    fetch(url , { method : 'delete'
					, headers : {'Content-Type' : 'application/json'}
					, body : JSON.stringify(json)
				})
    .then(res => res.json())
    .then(map => callback(map));
}

function fetchFormData(url, formData, callback) {
    fetch(url , { 
                    method : 'post',
                    body: formData,
				})
    .then(res => res.json())
    .then(map => callback(map));
}