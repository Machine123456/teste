document.addEventListener('DOMContentLoaded', function() {

    document.body.addEventListener('click', function (event) {
        if (
            !event.target.closest('.dropdown') &&
            !event.target.closest('.dropdown > *')
        ) {
            closeAllDropdowns();
        }
    });

    var currentUrl = window.location.href;
    var links = document.querySelectorAll("a");

    links.forEach(function (link) {
        var linkHref = link.getAttribute("href");
        var absoluteUrl = new URL(linkHref, currentUrl).href;
        if (absoluteUrl === currentUrl) {
            // Disable the link by preventing the default behavior
            link.addEventListener("click", function (event) {
                event.preventDefault();
            });
    
            // Optionally, you can style the disabled link
            link.style.color = "gray";
            link.style.textDecoration = "none";
            link.style.cursor= "default";
        }
    });


    fetch('/api/getUserFromToken')
    .then((response) => {
        if (!response.ok) {
            hideRequiredLoginComponents();
        throw new Error('Network response was not ok');
        }
        return response.json();
    })
    .then((user) => {
        if(!user){
            hideRequiredLoginComponents();
            return;
        }
    
    if (!user.authorities.includes("ROLE_ADMIN")) {
            hideRequiredAdminComponents();
        }

    const userName = document.getElementById("userName");
    const userEmail = document.getElementById("userEmail");
    
    userName.textContent = user.username;
    userEmail.textContent = user.email;
    
  })
  .catch((error) => {
      console.error("Error fetching user from token:", error);
      hideRequiredLoginComponents();
  });
});

function hideRequiredLoginComponents() {
     
    var reqComponent = document.querySelectorAll('.req-login');
    reqComponent.forEach(function (component) {
        component.style.display = "none";
    });

    hideRequiredAdminComponents();
}

function hideRequiredAdminComponents() {
     
    var reqComponent = document.querySelectorAll('.req-admin');
    reqComponent.forEach(function (component) {
        component.style.display = "none";
    });
}

function closeAllDropdowns() {
    var dropdowns = document.querySelectorAll('.dropdown-content.active');
    dropdowns.forEach(function (dropdown) {
        dropdown.classList.remove('active');
    });

    var images = document.querySelectorAll('*[id$="-image"].active');
    images.forEach(function (image) {
        image.classList.remove('active');
    });

    var rotImages = document.querySelectorAll('*[id$="-image"].rotate');
    rotImages.forEach(function (image) {
        image.classList.remove("rotate");
        image.classList.add("inv-rotate"); 
    });
}

function toggleDropdown(dropdownId, imageId) {

    var dropdownContent = document.getElementById(dropdownId);
    var image = document.getElementById(imageId);
   
    if (dropdownContent.classList.contains('active')) {
        dropdownContent.classList.remove('active');
        image.classList.remove('active');
    } else {
        closeAllDropdowns();
        dropdownContent.classList.add('active');
        image.classList.add('active');
    }
}


function toggleRotateDropdown(dropdownId, imageId) {


    var dropdownContent = document.getElementById(dropdownId);
    var image = document.getElementById(imageId);

    if (dropdownContent.classList.contains('active')) {
        image.classList.remove("rotate"); // Remove rotation class
        image.classList.add("inv-rotate"); // add inverse rotation class

        dropdownContent.classList.remove("active"); // Remove active class
    } else {
        closeAllDropdowns();
        image.classList.remove("inv-rotate"); // remove inverse rotation class
        image.classList.add("rotate"); // Add rotation class

        dropdownContent.classList.add("active"); // Add active class
    }
}

function logout(event) {
  event.preventDefault();

  const xhr = new XMLHttpRequest();
      
  xhr.open('POST', '/auth/logout', true);
  //const csrfToken = document.querySelector('input[name="_csrf"]').value;

  xhr.setRequestHeader('Content-Type', 'application/json');
  //xhr.setRequestHeader('X-CSRF-TOKEN', csrfToken); // Include CSRF token in the request headers
      
  // Set up a callback function to handle the response
  xhr.onreadystatechange = function() {
      if (xhr.readyState === XMLHttpRequest.DONE) {
          if(xhr.status === 200) {
              window.location.href = '/login'; 
          }
      }
  };
      
  xhr.send();
 
}